package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.util.LocalUtils
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.PlayerStatus
import java.text.SimpleDateFormat
import java.util.Locale


class AudioPlayerViewModel(private val trackUrl: String?) : ViewModel() {
    private val mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private var timer = LocalUtils().dateFormat(0)
    private val playerStatusLiveData = MutableLiveData(PlayerStatus())
    fun observePlayerStatus(): LiveData<PlayerStatus> = playerStatusLiveData
    private val timerRunnable = Runnable {
        val currentPosition = mediaPlayer.currentPosition
        playerStatusLiveData.value =
            playerStatusLiveData.value?.copy(timer = LocalUtils().dateFormat(currentPosition.toLong()))
        if (playerStatusLiveData.value?.isPlaying == true) {
            startTimerUpdate()
        }
    }

    init {
        preparePlayer()
    }

    fun onPause() {
        pausePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        resetTimer()
    }

    fun onPlayButtonClicked() {
        when (playerStatusLiveData.value?.playerState) {
            PlayerState.STATE_PLAYING -> {
                pausePlayer()
            }

            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                startPlayer()
            }

            else -> {}
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerStatusLiveData.postValue(
                PlayerStatus(
                    timer = timer,
                    playerState = PlayerState.STATE_PREPARED,
                    isPrepared = true
                )
            )
        }
        mediaPlayer.setOnCompletionListener {
            playerStatusLiveData.postValue(
                PlayerStatus(
                    timer = timer,
                    playerState = PlayerState.STATE_PREPARED,
                    isPrepared = true
                )
            )
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerStatusLiveData.postValue(
            PlayerStatus(
                timer = playerStatusLiveData.value?.timer ?: "00:00",
                playerState = PlayerState.STATE_PLAYING,
                isPlaying = true,
                isPrepared = true
            )
        )
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        playerStatusLiveData.postValue(
            PlayerStatus(
                timer = playerStatusLiveData.value?.timer ?: "00:00",
                playerState = PlayerState.STATE_PAUSED,
                isPrepared = true
            )
        )
    }

    private fun startTimerUpdate() {
        timer = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        handler.post(timerRunnable)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        playerStatusLiveData.postValue(PlayerStatus("00:00", PlayerState.STATE_PREPARED))
    }

    companion object {
        fun getFactory(trackUrl: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AudioPlayerViewModel(trackUrl)
            }
        }
    }
}