package com.example.playlistmaker.media.playlists.playlist.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalStorageInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.media.playlists.playlist.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.playlists.playlist.ui.models.TracksState
import com.example.playlistmaker.player.ui.models.TrackIds
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.LocalUtils
import com.google.gson.Gson
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private var tracksList: String,
    private val playlistsInteractor: PlaylistsInteractor,
    private val externalStorageInteractor: ExternalStorageInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    private val stateTracksLiveData = MutableLiveData<TracksState>()
    fun observeStateTracks(): LiveData<TracksState> = stateTracksLiveData

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData

    fun loadPlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(playlistId)
            playlistLiveData.postValue(playlist)
            tracksList = playlist.tracksList
            getTracks()
        }
    }

    fun getFullPath(path: String): Uri? {
        return externalStorageInteractor.loadImageFromStorage(path)
    }

    private fun renderState(state: TracksState) {
        stateTracksLiveData.postValue(state)
    }

    fun getTracks() {
        viewModelScope.launch {
            playlistsInteractor.getTracks(tracksList).collect { tracks ->
                progressResult(tracks)
            }
        }
    }

    fun sharePlaylist(playlist: Playlist?, tracks: List<Track>) {
        playlistInteractor.shareLink(playlist, tracks)
    }

    fun deleteTrack(clickedPlaylist: Playlist, currentTrack: Track) {
        viewModelScope.launch {
            playlistsInteractor.updateTrackIntoPlaylist(
                clickedPlaylist,
                currentTrack,
                getTracksIntoPlaylist(clickedPlaylist),
                true
            )
            val isUsedElsewhere = playlistsInteractor.isTrackUsedInAnyPlaylist(currentTrack.trackId)
            if (!isUsedElsewhere) {
                playlistsInteractor.deleteTrack(currentTrack)
            }
            tracksList = clickedPlaylist.tracksList
            getTracks()
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlist.id)
        }
    }

    fun getTracksIntoPlaylist(playlist: Playlist): MutableList<Long> {
        val json = playlist.tracksList
        return try {
            val gson = Gson()
            val trackIds = gson.fromJson(json, TrackIds::class.java)
            trackIds?.trackIds?.toMutableList() ?: mutableListOf()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    fun getTotalTime(tracks: List<Track>): String {
        var totalTimeMm: Long = 0
        tracks.map { track ->
            totalTimeMm += track.trackTimeMillis
        }
        return LocalUtils().dateMmFormat(totalTimeMm)
    }

    private fun progressResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(TracksState.Empty)
        } else {
            renderState(TracksState.Content(tracks))
        }
    }

}