package com.example.playlistmaker.domain.repository.audioplayer

interface MediaPlayerInteractor {
    fun playbackControl()
    fun preparePlayer(url: String?)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun isStatePlayerPlaying(): Boolean
    fun isStatePlayerPrepared(): Boolean
}
