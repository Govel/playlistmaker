package com.example.playlistmaker.data.repository.audioplayer

import android.media.MediaPlayer
import com.example.playlistmaker.domain.repository.audioplayer.MediaPlayerRepository

class MediaPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : MediaPlayerRepository {
    private var playerState = PlayerState.STATE_DEFAULT

    override fun playbackControl() {
        if (playerState == PlayerState.STATE_PLAYING) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    override fun preparePlayer(url: String?) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerState.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerState.STATE_PREPARED
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = PlayerState.STATE_PAUSED
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isStatePlayerPlaying(): Boolean {
        return playerState == PlayerState.STATE_PLAYING
    }

    override fun isStatePlayerPrepared(): Boolean {
        return playerState == PlayerState.STATE_PREPARED
    }

    companion object {
        enum class PlayerState {
            STATE_DEFAULT,
            STATE_PREPARED,
            STATE_PLAYING,
            STATE_PAUSED
        }
    }
}