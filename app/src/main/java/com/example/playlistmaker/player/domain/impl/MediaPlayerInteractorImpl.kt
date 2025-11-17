package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.repository.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.repository.MediaPlayerRepository

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