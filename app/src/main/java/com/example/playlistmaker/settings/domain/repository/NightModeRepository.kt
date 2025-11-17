package com.example.playlistmaker.settings.domain.repository

interface NightModeRepository {
    fun switchMode(isModeEnabled: Boolean)
    fun getSettingsValue(): Boolean
    fun setNightMode()
}
