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
    private var searchEditText: EditText? = null
    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private val adapter = TrackAdapter(tracks)
    private var lastCall: Call<TrackResponse>? = null

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val materialToolbar: MaterialToolbar = findViewById(R.id.title_search)
        val clearButton = findViewById<ImageView>(R.id.search_clearIcon)
        val searchEditText = findViewById<EditText>(R.id.search_bar)
        val rvSearchResult = findViewById<RecyclerView>(R.id.rvSearchResult)
        val searchIsEmpty = findViewById<LinearLayout>(R.id.search_is_empty)
        val searchNoInternet = findViewById<LinearLayout>(R.id.search_no_internet)
        val btSearchUpdate = findViewById<Button>(R.id.bt_search_update)

        rvSearchResult.adapter = adapter

        materialToolbar.setNavigationOnClickListener {
            finish()
        }
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
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
                            if (response.code() == 200) {
                                tracks.clear()
                                searchNoInternet.visibility = View.GONE
                                searchIsEmpty.visibility = View.GONE
                                if (response.body()?.results?.isNotEmpty() == true) {
                                    tracks.addAll(response.body()?.results!!)
                                    adapter.notifyDataSetChanged()
                                } else {
                                    tracks.clear()
                                    adapter.notifyDataSetChanged()
                                    searchNoInternet.visibility = View.GONE
                                    searchIsEmpty.visibility = View.VISIBLE
                                }
                            } else {
                                tracks.clear()
                                adapter.notifyDataSetChanged()
                                searchIsEmpty.visibility = View.GONE
                                searchNoInternet.visibility = View.VISIBLE
                            }
                        }

                        override fun onFailure(call: Call<TrackResponse?>, t: Throwable) {
                            tracks.clear()
                            adapter.notifyDataSetChanged()
                            searchIsEmpty.visibility = View.GONE
                            searchNoInternet.visibility = View.VISIBLE
                        }
                    })
                }
                true
            }
            false
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
                    if (response.code() == 200) {
                        tracks.clear()
                        searchNoInternet.visibility = View.GONE
                        searchIsEmpty.visibility = View.GONE
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        } else {
                            tracks.clear()
                            adapter.notifyDataSetChanged()
                            searchNoInternet.visibility = View.GONE
                            searchIsEmpty.visibility = View.VISIBLE
                        }
                    } else {
                        tracks.clear()
                        adapter.notifyDataSetChanged()
                        searchIsEmpty.visibility = View.GONE
                        searchNoInternet.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<TrackResponse?>, t: Throwable) {
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                    searchIsEmpty.visibility = View.GONE
                    searchNoInternet.visibility = View.VISIBLE
                }
            })
        }

        clearButton.setOnClickListener {
            searchEditText.setText("")
            searchEditText.clearFocus()
            tracks.clear()
            adapter.notifyDataSetChanged()
            searchNoInternet.visibility = View.GONE
            searchIsEmpty.visibility = View.GONE
            val imm =
                it.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString(EDIT_TEXT, editTextSaver)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextSaver = savedInstanceState.getString(EDIT_TEXT, TEXT_DEF)
        searchEditText?.setText(editTextSaver)
    }

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        const val TEXT_DEF = ""
    }

    private fun clearButtonVisability(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}