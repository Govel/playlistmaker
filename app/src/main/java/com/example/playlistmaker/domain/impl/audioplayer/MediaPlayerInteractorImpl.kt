package com.example.playlistmaker.domain.impl.audioplayer

import com.example.playlistmaker.domain.repository.audioplayer.MediaPlayerInteractor
import com.example.playlistmaker.domain.repository.audioplayer.MediaPlayerRepository

class MediaPlayerInteractorImpl(
    private val playerRepository: MediaPlayerRepository
) : MediaPlayerInteractor {
    override fun playbackControl() {
        playerRepository.playbackControl()
    }

    override fun preparePlayer(url: String?) {
        playerRepository.preparePlayer(url)
    }

    override fun startPlayer() {
        playerRepository.startPlayer()
    }

    override fun pausePlayer() {
        playerRepository.pausePlayer()
    }

    override fun releasePlayer() {
        playerRepository.releasePlayer()
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

    override fun isStatePlayerPlaying(): Boolean {
        return playerRepository.isStatePlayerPlaying()
    }

    override fun isStatePlayerPrepared(): Boolean {
        return playerRepository.isStatePlayerPrepared()
    }
}