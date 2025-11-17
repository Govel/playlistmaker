package com.example.playlistmaker.settings.domain.repository

interface NightModeInteractor {
    fun switchMode(isModeEnabled: Boolean)
    fun getSettingsValue(): Boolean
    fun setNightMode()
}
