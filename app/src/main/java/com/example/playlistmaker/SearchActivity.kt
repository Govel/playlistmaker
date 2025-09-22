package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var editTextSaver: String = TEXT_DEF
    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private val tracksHistory = mutableListOf<Track>()
    private var adapter = TrackAdapter(tracks) { clickedTrack ->
        searchHistory.push(clickedTrack)
        loadSearchHistory()
    }
    private var adapterHistory = TrackAdapter(tracks) { clickedTrack ->
        if (clickDebounce()) {
            searchHistory.push(clickedTrack)
            loadSearchHistory()
        }
    }
    private var lastCall: Call<TrackResponse>? = null
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
    private lateinit var searchHistory: SearchHistory
    private lateinit var pbSearch: FrameLayout
    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchRequest() }
    private var isClickAllowed = true
    companion object {
        private const val SHARED_PREFERENСES = "shared_prefs"
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }

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
        clearButton = findViewById<ImageView>(R.id.search_clearIcon)
        searchEditText = findViewById<EditText>(R.id.search_bar)
        rvSearchResult = findViewById<RecyclerView>(R.id.rv_search_result)
        rvSearchHistory = findViewById<RecyclerView>(R.id.rv_search_history)
        btSearchUpdate = findViewById<Button>(R.id.bt_search_update)
        btClearHistory = findViewById<Button>(R.id.bt_clear_history)
        llSearchIsEmpty = findViewById<LinearLayout>(R.id.search_is_empty)
        llSearchNoInternet = findViewById<LinearLayout>(R.id.search_no_internet)
        llSearchHistory = findViewById<LinearLayout>(R.id.ll_search_history)
        pbSearch = findViewById<FrameLayout>(R.id.pb_search)
        val sharedPrefs = getSharedPreferences(SHARED_PREFERENСES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        loadSearchHistory()

        adapter = TrackAdapter(tracks) { clickedTrack ->
            if (clickDebounce()) {
                searchHistory.push(clickedTrack)
                loadSearchHistory()
                val displayAudioPlayer = Intent(this, AudioPlayer::class.java)
                displayAudioPlayer.putExtra(TAG_CURRENT_TRACK, clickedTrack)
                startActivity(displayAudioPlayer)
            }
        }

        adapterHistory = TrackAdapter(tracksHistory) { clickedTrack ->
            if (clickDebounce()) {
                searchHistory.push(clickedTrack)
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
                clearButton.isVisible = clearButtonVisability(s)
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                editTextSaver = s?.toString() ?: ""
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchRequest()
                true
            }
            false
        }
        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            llSearchHistory.isVisible = !hasFocus
        }
        btSearchUpdate.setOnClickListener {
            val retryCall = lastCall?.clone()
            llSearchNoInternet.isVisible = false
            pbSearch.isVisible = true
            lastCall = retryCall
            retryCall?.enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse?>,
                    response: Response<TrackResponse?>
                ) {
                    pbSearch.isVisible = false
                    handleSearchResponse(response)
                }

                override fun onFailure(call: Call<TrackResponse?>, t: Throwable) {
                    pbSearch.isVisible = false
                    handleSearchFailure()
                }
            })
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
            searchHistory.clear()
            tracksHistory.clear()
            adapterHistory.notifyDataSetChanged()
            llSearchHistory.isVisible = false
        }
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

    private fun clearButtonVisability(s: CharSequence?): Boolean {
        return if (s.isNullOrEmpty()) false else true
    }

    private fun searchRequest() {
        if (searchEditText.text.isNotEmpty()) {
            showProgressBar()
            lastCall = iTunesService.search(searchEditText.text.toString())
            lastCall?.enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse?>,
                    response: Response<TrackResponse?>
                ) {
                    handleSearchResponse(response)
                }

                override fun onFailure(call: Call<TrackResponse?>, t: Throwable) {
                    showProgressBar()
                    handleSearchFailure()
                }
            })
        }
    }
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    private fun handleSearchResponse(response: Response<TrackResponse?>) {
        if (response.isSuccessful) { // response.code() in 200..299
            val results = response.body()?.results
            if (!results.isNullOrEmpty()) {
                tracks.clear()
                tracks.addAll(results)
                adapter.notifyDataSetChanged()
                showContent()
            } else {
                showEmptyResults()
            }
        } else {
            showNetworkError()
        }
    }

    private fun handleSearchFailure() {
        showNetworkError()
    }

    private fun showContent() {
        llSearchHistory.isVisible = false
        rvSearchResult.isVisible = true
        llSearchIsEmpty.isVisible = false
        llSearchNoInternet.isVisible = false
        pbSearch.isVisible = false
    }

    private fun showEmptyResults() {
        tracks.clear()
        adapter.notifyDataSetChanged()
        llSearchHistory.isVisible = false
        rvSearchResult.isVisible = false
        llSearchIsEmpty.isVisible = true
        llSearchNoInternet.isVisible = false
        pbSearch.isVisible = false
    }

    private fun showNetworkError() {
        tracks.clear()
        adapter.notifyDataSetChanged()
        llSearchHistory.isVisible = false
        rvSearchResult.isVisible = false
        llSearchIsEmpty.isVisible = false
        llSearchNoInternet.isVisible = true
        pbSearch.isVisible = false
    }
    private fun showHistory() {
        tracks.clear()
        adapter.notifyDataSetChanged()
        loadSearchHistory()
        rvSearchResult.isVisible = false
        llSearchIsEmpty.isVisible = false
        llSearchNoInternet.isVisible = false
        pbSearch.isVisible = false
    }
    private fun showProgressBar() {
        tracks.clear()
        adapter.notifyDataSetChanged()
        llSearchHistory.isVisible = false
        rvSearchResult.isVisible = false
        llSearchIsEmpty.isVisible = false
        llSearchNoInternet.isVisible = false
        pbSearch.isVisible = true
    }

    private fun loadSearchHistory() {
        val saved = searchHistory.get()
        tracksHistory.clear()
        tracksHistory.addAll(saved)
        adapterHistory.notifyDataSetChanged()
        llSearchHistory.isVisible = tracksHistory.isNotEmpty()
    }
}
