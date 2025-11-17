package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.ui.AudioPlayer
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.consumer.Consumer
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.TAG_CURRENT_TRACK
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Runnable

class SearchActivity : AppCompatActivity() {
    private val searchRequestRunnable = Runnable { searchRequest() }
    private var editTextSaver: String = TEXT_DEF
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var tracksInteractor = Creator.provideTracksInteractor(SHARED_PREFERENCES)
    private val tracksSearch = ArrayList<Track>()
    private val tracksHistory = mutableListOf<Track>()
    private var adapter = TrackAdapter(tracksSearch) { clickedTrack ->
        tracksInteractor.saveTrackToHistory(clickedTrack)
        loadSearchHistory()
    }
    private var adapterHistory = TrackAdapter(tracksHistory) { clickedTrack ->
        if (clickDebounce()) {
            tracksInteractor.saveTrackToHistory(clickedTrack)
            loadSearchHistory()
        }
    }
    private lateinit var materialToolbar: MaterialToolbar
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var rvSearchResult: RecyclerView
    private lateinit var rvSearchHistory: RecyclerView
    private lateinit var btSearchUpdate: Button
    private lateinit var btClearHistory: Button
    private lateinit var llSearchIsEmpty: LinearLayout
    private lateinit var llSearchNoInternet: LinearLayout
    private lateinit var llSearchHistory: LinearLayout
    private lateinit var pbSearch: FrameLayout
    private var isClickAllowed = true

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = if (ime.bottom > 0) {
                ime.bottom
            } else {
                systemBars.bottom
            }
            v.setPadding(
                systemBars.left, systemBars.top, systemBars.right,
                bottomPadding
            )
            insets
        }
        materialToolbar = findViewById(R.id.title_search)
        clearButton = findViewById(R.id.search_clearIcon)
        searchEditText = findViewById(R.id.search_bar)
        rvSearchResult = findViewById(R.id.rv_search_result)
        rvSearchHistory = findViewById(R.id.rv_search_history)
        btSearchUpdate = findViewById(R.id.bt_search_update)
        btClearHistory = findViewById(R.id.bt_clear_history)
        llSearchIsEmpty = findViewById(R.id.search_is_empty)
        llSearchNoInternet = findViewById(R.id.search_no_internet)
        llSearchHistory = findViewById(R.id.ll_search_history)
        pbSearch = findViewById(R.id.pb_search)
        loadSearchHistory()

        adapter = TrackAdapter(tracksSearch) { clickedTrack ->
            if (clickDebounce()) {
                tracksInteractor.saveTrackToHistory(clickedTrack)
                loadSearchHistory()
                val displayAudioPlayer = Intent(this, AudioPlayer::class.java)
                displayAudioPlayer.putExtra(TAG_CURRENT_TRACK, clickedTrack)
                startActivity(displayAudioPlayer)
            }
        }

        adapterHistory = TrackAdapter(tracksHistory) { clickedTrack ->
            if (clickDebounce()) {
                tracksInteractor.saveTrackToHistory(clickedTrack)
                loadSearchHistory()
                val displayAudioPlayer = Intent(this, AudioPlayer::class.java)
                displayAudioPlayer.putExtra(TAG_CURRENT_TRACK, clickedTrack)
                startActivity(displayAudioPlayer)
            }
        }
        rvSearchResult.adapter = adapter
        rvSearchHistory.adapter = adapterHistory

        if (searchEditText.text.isEmpty()) {
            if (tracksHistory.isNotEmpty()) {
                showHistory()
            } else {
                llSearchHistory.isVisible = false
            }
        }

        materialToolbar.setNavigationOnClickListener {
            finish()
        }
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                llSearchHistory.isVisible = !searchEditText.hasFocus()
                clearButton.isVisible = clearButtonVisibility(s)

                if (s?.isEmpty() == false) {
                    searchDebounce()
                } else {
                    stopCurrentRunnable()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                editTextSaver = s?.toString() ?: ""
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRequest()
            }
            false
        }
        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            llSearchHistory.isVisible = !hasFocus
        }
        btSearchUpdate.setOnClickListener {
            searchRequest()
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            searchEditText.clearFocus()
            showHistory()
            val imm =
                it.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        btClearHistory.setOnClickListener {
            tracksInteractor.clearHistory()
            tracksHistory.clear()
            adapterHistory.notifyDataSetChanged()
            llSearchHistory.isVisible = false
        }
    }

    private fun searchDebounce() {
        stopCurrentRunnable()
        handler.postDelayed(searchRequestRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString(EDIT_TEXT, editTextSaver)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextSaver = savedInstanceState.getString(EDIT_TEXT, TEXT_DEF)
        searchEditText.setText(editTextSaver)
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun stopCurrentRunnable() {
        val currentRunnable = searchRunnable
        if (currentRunnable != null) {
            handler.removeCallbacks(currentRunnable)
        }
        handler.removeCallbacks(searchRequestRunnable)
    }

    private fun searchRequest() {
        Log.d("MyTag", tracksInteractor.toString())
        if (editTextSaver.isNotEmpty()) {
            showProgressBar()
            tracksInteractor.searchTracks(
                expression = editTextSaver,
                consumer = object : Consumer {
                    override fun consume(tracks: Resource<List<Track>?>) {
                        val newSearchRunnable = Runnable {
                            if (tracks.data != null && tracks.expression == editTextSaver) {
                                if (tracks.data.isNotEmpty()) {
                                    tracksSearch.clear()
                                    tracksSearch.addAll(tracks.data)
                                    adapter.notifyDataSetChanged()
                                    showContent()
                                } else {
                                    showEmptyResults()
                                }
                            } else if (tracks.data == null) {
                                showNetworkError()
                            } else {
                                stopCurrentRunnable()
                            }
                        }
                        searchRunnable = newSearchRunnable
                        handler.post(newSearchRunnable)
                    }
                })
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun showContent() {
        llSearchHistory.isVisible = false
        rvSearchResult.isVisible = true
        llSearchIsEmpty.isVisible = false
        llSearchNoInternet.isVisible = false
        pbSearch.isVisible = false
    }

    private fun showEmptyResults() {
        tracksSearch.clear()
        adapter.notifyDataSetChanged()
        llSearchHistory.isVisible = false
        rvSearchResult.isVisible = false
        llSearchIsEmpty.isVisible = true
        llSearchNoInternet.isVisible = false
        pbSearch.isVisible = false
    }

    private fun showNetworkError() {
        tracksSearch.clear()
        adapter.notifyDataSetChanged()
        llSearchHistory.isVisible = false
        rvSearchResult.isVisible = false
        llSearchIsEmpty.isVisible = false
        llSearchNoInternet.isVisible = true
        pbSearch.isVisible = false
    }

    private fun showHistory() {
        tracksSearch.clear()
        adapter.notifyDataSetChanged()
        loadSearchHistory()
        rvSearchResult.isVisible = false
        llSearchIsEmpty.isVisible = false
        llSearchNoInternet.isVisible = false
        pbSearch.isVisible = false
    }

    private fun showProgressBar() {
        tracksSearch.clear()
        adapter.notifyDataSetChanged()
        llSearchHistory.isVisible = false
        rvSearchResult.isVisible = false
        llSearchIsEmpty.isVisible = false
        llSearchNoInternet.isVisible = false
        pbSearch.isVisible = true
    }

    private fun loadSearchHistory() {
        val saved = tracksInteractor.loadTracksFromHistory()
        tracksHistory.clear()
        tracksHistory.addAll(saved)
        adapterHistory.notifyDataSetChanged()
        llSearchHistory.isVisible = tracksHistory.isNotEmpty()
    }

    companion object {
        private const val SHARED_PREFERENCES = "shared_prefs"
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
