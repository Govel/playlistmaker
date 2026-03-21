package com.example.playlistmaker.player.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker.db.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalStorageInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.services.musicservice.api.AudioPlayerControl
import com.example.playlistmaker.player.ui.models.IsFavoriteTrack
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.TrackIds
import com.example.playlistmaker.player.ui.models.TrackOnPlaylistState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AudioPlayerViewModel(
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val externalStorageInteractor: ExternalStorageInteractor
) : ViewModel() {
    private var playerService: AudioPlayerControl? = null

    private val isFavoriteLiveData = MutableLiveData<IsFavoriteTrack>()
    fun observeIsFavoriteTrack(): LiveData<IsFavoriteTrack> = isFavoriteLiveData

    private val trackOnPlaylistStateLiveData = MutableLiveData<TrackOnPlaylistState>()
    fun observeTrackOnPlaylistState(): LiveData<TrackOnPlaylistState> = trackOnPlaylistStateLiveData

    private val isInPlaylist = MutableLiveData<Pair<String, Boolean>>()
    fun observeIsInPlaylist(): LiveData<Pair<String, Boolean>> = isInPlaylist

    fun attachService(service: AudioPlayerControl) {
        playerService = service
    }

    fun detachService() {
        playerService = null
    }


    fun observePlayerState(): LiveData<PlayerState>? {
        val flow: StateFlow<PlayerState> = playerService?.getPlayerState() ?: return null
        return flow.asLiveData()
    }

    fun onPlayButtonClicked() {
        when (playerService?.getPlayerState()?.value) {
            is PlayerState.Playing -> playerService?.pausePlayer()
            is PlayerState.Prepared,
            is PlayerState.Paused -> playerService?.startPlayer()

            else -> Unit
        }
    }

    fun onAppBackgrounded() {
        val state = playerService?.getPlayerState()?.value
        if (state is PlayerState.Playing) {
            playerService?.showNotification()
        }
    }

    fun onAppForegrounded() {
        playerService?.hideNotification()
    }

    fun onScreenClosed() {
        playerService?.hideNotification()
//        playerService?.stopPlayback()
    }

    fun checkInitialFavoriteState(trackId: Long) {
        viewModelScope.launch {
            val isFavorite = favoriteTrackInteractor.isTrackFavorite(trackId)
            isFavoriteLiveData.postValue(IsFavoriteTrack(isFavorite))
        }
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch {
            val isCurrentlyFavorite = favoriteTrackInteractor.isTrackFavorite(track.trackId)
            if (isCurrentlyFavorite) {
                favoriteTrackInteractor.deleteFavoriteTrack(track)
            } else {
                favoriteTrackInteractor.addFavoriteTrack(track)
            }
            val newState = !isCurrentlyFavorite
            isFavoriteLiveData.postValue(IsFavoriteTrack(newState))
        }
    }

    fun showPlaylists() {
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(TrackOnPlaylistState.Empty)
        } else {
            renderState(TrackOnPlaylistState.Content(playlists))
        }
    }

    private fun renderState(state: TrackOnPlaylistState) {
        trackOnPlaylistStateLiveData.postValue(state)
    }

    fun isOnPlaylist(clickedPlaylist: Playlist, currentTrack: Track) {
        val tracksList = getTracksIntoPlaylist(clickedPlaylist)
        if (!tracksList.contains(currentTrack.trackId)) {
            isInPlaylist.postValue(Pair(clickedPlaylist.name, true))
            viewModelScope.launch {
                playlistsInteractor.updateTrackIntoPlaylist(
                    clickedPlaylist,
                    currentTrack,
                    tracksList,
                    false
                )
                playlistsInteractor.addTrackIntoPlaylists(currentTrack)
                showPlaylists()
            }
        } else {
            isInPlaylist.postValue(Pair(clickedPlaylist.name, false))
        }
    }

    private fun getTracksIntoPlaylist(playlist: Playlist): MutableList<Long> {
        val json = playlist.tracksList
        return try {
            val gson = Gson()
            val trackIds = gson.fromJson(json, TrackIds::class.java)
            trackIds?.trackIds?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    fun getUriForCover(coverName: String): Uri? {
        return try {
            externalStorageInteractor.loadImageFromStorage(coverName)
        } catch (e: Exception) {
            Log.e("PlaylistsViewModel", "Ошибка загрузки обложки: $coverName", e)
            null
        }
    }
}
