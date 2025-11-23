package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.consumer.Consumer
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.repository.TracksInteractor
import com.example.playlistmaker.search.ui.model.SearchMessage
import kotlinx.coroutines.Runnable

class SearchViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {
    private val stateSearchLiveData = MutableLiveData<SearchState>()
    fun observeStateSearch(): LiveData<SearchState> = stateSearchLiveData
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private var latestSearchText: String? = null

    fun searchDebounce(changedText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        val searchRunnable = Runnable { searchRequest(changedText) }
        if (latestSearchText == changedText) {
            handler.post(searchRunnable)
        } else {
            val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
            handler.postAtTime(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                postTime,
            )
        }
        this.latestSearchText = changedText
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
            tracksInteractor.searchTracks(
                expression = newSearchText,
                consumer = object : Consumer {
                    override fun consume(foundTracks: Resource<List<Track>?>) {
                        val tracks = mutableListOf<Track>()
                        val newSearchRunnable = Runnable {
                            if (foundTracks.data != null && foundTracks.expression == newSearchText && foundTracks.message != SearchMessage.ERROR.toString()) {
                                tracks.clear()
                                tracks.addAll(foundTracks.data)
                            }
                            when {
                                tracks.isEmpty() && foundTracks.message == SearchMessage.EMPTY.toString() -> {
                                    renderState(SearchState.Empty(foundTracks.message))
                                }

                                tracks.isEmpty() && foundTracks.message == SearchMessage.ERROR.toString() -> {
                                    renderState(SearchState.Error(foundTracks.message))
                                }

                                else -> {
                                    renderState(SearchState.Content(tracks))
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

    fun saveTrackToHistory(clickedTrack: Track) = tracksInteractor.saveTrackToHistory(clickedTrack)

    fun clearHistory() = tracksInteractor.clearHistory()

    fun loadTracksFromHistory(): Collection<Track> = tracksInteractor.loadTracksFromHistory()

    companion object {
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val provideTracksInteractor = Creator.provideTracksInteractor(SHARED_PREFERENCES)
                SearchViewModel(provideTracksInteractor)
            }
        }

        private const val SHARED_PREFERENCES = "shared_prefs"
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}