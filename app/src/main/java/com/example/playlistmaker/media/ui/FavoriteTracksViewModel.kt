package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.db.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteState>()

    fun observeState(): LiveData<FavoriteState> = stateLiveData

    fun showFavoriteTracks() {
        renderState(FavoriteState.Loading)
        viewModelScope.launch {
            favoriteTrackInteractor
                .getFavoriteTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteState.Empty(R.string.no_favorite_tracks.toString()))
        } else {
            renderState(FavoriteState.Content(tracks))
        }
    }

    private fun renderState(state: FavoriteState) {
        stateLiveData.postValue(state)
    }
}