package com.example.playlistmaker.domain.repository.settings

interface NightModeInteractor {
    fun switchMode(isModeEnabled: Boolean)
    fun getSettingsValue(): Boolean
    fun setNightMode()
}
