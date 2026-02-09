package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker.db.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalStorageInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.model.Playlist
import com.example.playlistmaker.player.ui.models.IsFavoriteTrack
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.TrackIds
import com.example.playlistmaker.player.ui.models.TrackOnPlaylistState
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerViewModel(
    private val trackUrl: String?,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val externalStorageInteractor: ExternalStorageInteractor
) : ViewModel() {
    private val mediaPlayer = MediaPlayer()
    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val isFavoriteLiveData = MutableLiveData<IsFavoriteTrack>()
    fun observeIsFavoriteTrack(): LiveData<IsFavoriteTrack> = isFavoriteLiveData

    private val trackOnPlaylistStateLiveData = MutableLiveData<TrackOnPlaylistState>()

    fun observeTrackOnPlaylistState(): LiveData<TrackOnPlaylistState> = trackOnPlaylistStateLiveData

    private val isInPlaylist = MutableLiveData<Pair<String,Boolean>>()

    fun observeIsInPlaylist(): LiveData<Pair<String,Boolean>> = isInPlaylist

    init {
        preparePlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(
                PlayerState.Prepared()
            )
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerStateLiveData.postValue(
                PlayerState.Prepared()
            )
        }
    }

    private fun startPlayer() {
        if (playerStateLiveData.value is PlayerState.Prepared) {
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.start()
        playerStateLiveData.postValue(
            PlayerState.Playing(getCurrentPlayerPosition())
        )
        startTimerUpdate()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(
            PlayerState.Paused(getCurrentPlayerPosition())
        )
    }

    private fun releasePlayer() {
        timerJob?.cancel()

        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        mediaPlayer.release()
        playerStateLiveData.value = PlayerState.Default()
    }

    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(TIMER_UPDATE_DELAY)
                if (mediaPlayer.isPlaying) {
                    if (playerStateLiveData.value is PlayerState.Playing)
                        playerStateLiveData.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
                }
            }
        }
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

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            ?: "00:00"
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
                playlistsInteractor.updatePlaylist(clickedPlaylist, currentTrack, tracksList)
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


    companion object {
        const val TIMER_UPDATE_DELAY = 300L
    }
}
