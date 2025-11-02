package com.example.playlistmaker.data.repository.audioplayer

import android.media.MediaPlayer
import com.example.playlistmaker.domain.repository.audioplayer.MediaPlayerRepository

class MediaPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : MediaPlayerRepository {
    private var playerState = STATE_DEFAULT

    override fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun preparePlayer(url: String?) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isStatePlayerPlaying(): Boolean {
        return playerState == STATE_PLAYING
    }

    override fun isStatePlayerPrepared(): Boolean {
        return playerState == STATE_PREPARED
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}