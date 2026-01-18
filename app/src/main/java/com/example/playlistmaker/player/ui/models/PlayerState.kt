package com.example.playlistmaker.player.ui.models

//enum class PlayerState {
//    STATE_DEFAULT,
//    STATE_PREPARED,
//    STATE_PLAYING,
//    STATE_PAUSED
//}

sealed class PlayerState(val isPlayButtonEnabled: Boolean, val buttonText: String, val progress: String) {
    class Default : PlayerState(false, "PLAY", "00:00")
    class Prepared : PlayerState(true, "PLAY", "00:00")
    class Playing(progress: String) : PlayerState(true, "PAUSE", progress)
    class Paused(progress: String) : PlayerState(true, "PLAY", progress)
}