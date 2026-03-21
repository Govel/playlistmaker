package com.example.playlistmaker.services.musicservice.api

import com.example.playlistmaker.player.ui.models.PlayerState
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun getPlayerState(): StateFlow<PlayerState>
    fun startPlayer()
    fun pausePlayer()
    fun showNotification()
    fun hideNotification()
}