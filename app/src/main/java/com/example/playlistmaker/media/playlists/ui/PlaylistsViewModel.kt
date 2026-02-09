package com.example.playlistmaker.media.playlists.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalStorageInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.model.Playlist
import com.example.playlistmaker.media.playlists.ui.models.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val externalStorageInteractor: ExternalStorageInteractor
): ViewModel() {
    private val statePlaylistsLiveData = MutableLiveData<PlaylistsState>()
    fun observeStatePlaylists(): LiveData<PlaylistsState> = statePlaylistsLiveData

    private fun renderState(state: PlaylistsState) {
        statePlaylistsLiveData.postValue(state)
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

    fun getUriForCover(coverName: String): Uri? {
        return try {
            externalStorageInteractor.loadImageFromStorage(coverName)
        } catch (e: Exception) {
            Log.e("PlaylistsViewModel", "Ошибка загрузки обложки: $coverName", e)
            null
        }
    }


    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistsState.Empty)
        } else {
            renderState(PlaylistsState.Content(playlists))
        }
    }
}