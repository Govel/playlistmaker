package com.example.playlistmaker.domain.repository.audioplayer

interface MediaPlayerRepository {
    fun playbackControl()
    fun preparePlayer(url: String?)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun isStatePlayerPlaying(): Boolean
    fun isStatePlayerPrepared(): Boolean
}
