package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.repository.TracksInteractor
import com.example.playlistmaker.search.ui.model.SearchMessage
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {
    private var isSearchInProgress = false
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

    fun searchImmediately(query: String) {
        latestSearchText = query
        searchRequest(query)
    }

    private fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty() && !isSearchInProgress) {
            isSearchInProgress = true
            renderState(SearchState.Loading)
            viewModelScope.launch {
                tracksInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        processResult(pair.first, pair.second)
                        isSearchInProgress = false
                    }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
            viewModelScope.launch {
                favoriteTrackInteractor
                    .getFavoriteTrackId()
                    .collect { favoriteTracks ->
                        favoriteTrackProcessResult(favoriteTracks, tracks)
                    }
            }
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

    private fun favoriteTrackProcessResult(favoriteTracks: List<Track>, tracks: List<Track>) {
        if (favoriteTracks.isNotEmpty()) {
            favoriteTracks.forEach { favoriteTrack ->
                tracks.forEach { track ->
                    if (track.trackId == favoriteTrack.trackId) {
                        track.isFavorite = true
                    }
                }
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateSearchLiveData.postValue(state)
    }

    fun saveTrackToHistory(clickedTrack: Track) = tracksInteractor.saveTrackToHistory(clickedTrack)

    fun clearHistory() = tracksInteractor.clearHistory()

    fun loadTracksFromHistory(): List<Track> {
        val tracks = tracksInteractor.loadTracksFromHistory()
        viewModelScope.launch {
            favoriteTrackInteractor
                .getFavoriteTrackId()
                .collect { favoriteTracks ->
                    favoriteTrackProcessResult(favoriteTracks, tracks)
                }
        }
        return tracks
    }

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}