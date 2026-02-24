package com.example.playlistmaker.player.ui.models

sealed class PlayerState(val isPlayButtonEnabled: Boolean, val isPlaying: Boolean, val progress: String) {
    class Default : PlayerState(false, PLAY, TIMER_START)
    class Prepared : PlayerState(true, PLAY, TIMER_START)
    class Playing(progress: String) : PlayerState(true, PAUSE, progress)
    class Paused(progress: String) : PlayerState(true, PLAY, progress)

    companion object {
        const val PLAY = false
        const val PAUSE = true
        const val TIMER_START = "00:00"
    }
}