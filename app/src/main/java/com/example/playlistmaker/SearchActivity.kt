package com.example.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
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
    private val adapterHistory = HistoryTrackAdapter(tracksHistory)
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
    companion object {
        private const val SHARED_PREFERENСES = "shared_prefs"
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
    }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
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
        val sharedPrefs = getSharedPreferences(SHARED_PREFERENСES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        loadSearchHistory()

        adapter = TrackAdapter(tracks) { clickedTrack ->
            searchHistory.push(clickedTrack)
            loadSearchHistory()
        }

        rvSearchResult.adapter = adapter
        rvSearchHistory.adapter = adapterHistory

        if (searchEditText.text.isEmpty()) {
            if (tracksHistory.isNotEmpty()) {
                showHistory()
            } else {
                llSearchHistory.visibility = View.GONE
            }
        }

        materialToolbar.setNavigationOnClickListener {
            finish()
        }
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                llSearchHistory.visibility = if (searchEditText.hasFocus()) View.GONE else View.VISIBLE
                clearButton.visibility = clearButtonVisability(s)
            }

            override fun afterTextChanged(s: Editable?) {
                editTextSaver = s?.toString() ?: ""
            }
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchEditText.text.isNotEmpty()) {
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
                            handleSearchFailure()
                        }
                    })
                }
                true
            }
            false
        }
        searchEditText.setOnFocusChangeListener { view, hasFocus ->
            llSearchHistory.visibility = if (hasFocus) View.GONE else View.VISIBLE
        }
        btSearchUpdate.setOnClickListener {
            val retryCall = lastCall?.clone()
            lastCall = retryCall
            retryCall?.enqueue(object : Callback<TrackResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TrackResponse?>,
                    response: Response<TrackResponse?>
                ) {
                    handleSearchResponse(response)
                }

                override fun onFailure(call: Call<TrackResponse?>, t: Throwable) {
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
            llSearchHistory.visibility = View.GONE
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

    private fun clearButtonVisability(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
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
        llSearchHistory.visibility = View.GONE
        rvSearchResult.visibility = View.VISIBLE
        llSearchIsEmpty.visibility = View.GONE
        llSearchNoInternet.visibility = View.GONE
    }

    private fun showEmptyResults() {
        tracks.clear()
        adapter.notifyDataSetChanged()
        llSearchHistory.visibility = View.GONE
        rvSearchResult.visibility = View.GONE
        llSearchIsEmpty.visibility = View.VISIBLE
        llSearchNoInternet.visibility = View.GONE
    }

    private fun showNetworkError() {
        tracks.clear()
        adapter.notifyDataSetChanged()
        llSearchHistory.visibility = View.GONE
        rvSearchResult.visibility = View.GONE
        llSearchIsEmpty.visibility = View.GONE
        llSearchNoInternet.visibility = View.VISIBLE
    }
    private fun showHistory() {
        tracks.clear()
        adapter.notifyDataSetChanged()
        loadSearchHistory()
        rvSearchResult.visibility = View.GONE
        llSearchIsEmpty.visibility = View.GONE
        llSearchNoInternet.visibility = View.GONE
    }

    private fun loadSearchHistory() {
        val saved = searchHistory.get()
        tracksHistory.clear()
        tracksHistory.addAll(saved)
        adapterHistory.notifyDataSetChanged()
        llSearchHistory.visibility = if (tracksHistory.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
