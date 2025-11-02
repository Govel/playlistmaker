package com.example.playlistmaker.domain.repository.settings

interface NightModeRepository {
    fun switchMode(isModeEnabled: Boolean)
    fun getSettingsValue(): Boolean
    fun setNightMode()
}
