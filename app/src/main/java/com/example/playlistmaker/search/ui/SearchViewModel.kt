package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.App
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.consumer.Consumer
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Runnable

class SearchViewModel(private val context: Context): ViewModel() {
    private val stateSearchLiveData = MutableLiveData<SearchState>()
    fun observeStateSearch(): LiveData<SearchState> = stateSearchLiveData
    private val handler = Handler(Looper.getMainLooper())
    private var tracksInteractor = Creator.provideTracksInteractor(SHARED_PREFERENCES)
    private var searchRunnable: Runnable? = null

    private var latestSearchText: String? = null

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
            tracksInteractor.searchTracks(
                expression = newSearchText,
                consumer = object : Consumer {
                    override fun consume(foundTracks: Resource<List<Track>?>) {
                        Log.d("MyTag", "newSearchText: $newSearchText, foundTracks.expression: ${foundTracks.expression}")
                        Log.d("MyTag", "foundTracks: ${foundTracks.data}")
                        val tracks = mutableListOf<Track>()
                        val newSearchRunnable = Runnable {
                            if (foundTracks.data != null && foundTracks.expression == newSearchText) {
                                tracks.clear()
                                tracks.addAll(foundTracks.data)
                            }
                            when {
                                tracks.isEmpty() ->{
                                    renderState(SearchState.Empty("Empty"))
                                    Log.d("MyTag", "state Empty")
                                }



                                else -> {
                                    renderState(SearchState.Content(tracks))
                                    Log.d("MyTag", "state Content")
                                }
                            }
                        }
                        searchRunnable = newSearchRunnable
                        handler.post(newSearchRunnable)
                    }
                })
        }
    }

    private fun renderState(state: SearchState) {
        stateSearchLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[APPLICATION_KEY] as App)
                SearchViewModel(app)
            }
        }
        private const val SHARED_PREFERENCES = "shared_prefs"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}