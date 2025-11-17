package com.example.playlistmaker.player.domain.repository

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
