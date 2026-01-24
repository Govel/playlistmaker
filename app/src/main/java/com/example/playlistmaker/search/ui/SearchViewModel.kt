package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.repository.TracksInteractor
import com.example.playlistmaker.search.ui.model.SearchMessage
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor
) : ViewModel() {
    private val stateSearchLiveData = MutableLiveData<SearchState>()
    fun observeStateSearch(): LiveData<SearchState> = stateSearchLiveData
    private val tracksSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { changedText -> searchRequest(changedText) }
    private var latestSearchText: String? = null

    fun searchDebounce(changedText: String) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            tracksSearchDebounce(changedText)
        }
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)
            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }
        when {
            tracks.isEmpty() && errorMessage == SearchMessage.EMPTY.toString() -> {
                renderState(SearchState.Empty(errorMessage))
            }

            tracks.isEmpty() && errorMessage == SearchMessage.ERROR.toString() -> {
                renderState(SearchState.Error(errorMessage))
            }

            else -> {
                renderState(SearchState.Content(tracks))
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateSearchLiveData.postValue(state)
    }

    fun saveTrackToHistory(clickedTrack: Track) = tracksInteractor.saveTrackToHistory(clickedTrack)

    fun clearHistory() = tracksInteractor.clearHistory()

    fun loadTracksFromHistory(): Collection<Track> = tracksInteractor.loadTracksFromHistory()

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}