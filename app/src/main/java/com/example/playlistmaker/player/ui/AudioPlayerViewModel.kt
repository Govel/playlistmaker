package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerViewModel(
    private val trackUrl: String?,
    private val favoriteTrackInteractor: FavoriteTrackInteractor
) : ViewModel() {
    private val mediaPlayer = MediaPlayer()
    private var timerJob: Job? = null
    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

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

    private fun onFavoriteClicked(track: Track): Flow<Unit> = flow {
        if (!track.isFavorite) {
            viewModelScope.launch {
                favoriteTrackInteractor
                    .addFavoriteTrack(track).collect {
                        it
                    }
            }

        }
    }


    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            ?: "00:00"
    }


    companion object {
        const val TIMER_UPDATE_DELAY = 300L
    }
}
