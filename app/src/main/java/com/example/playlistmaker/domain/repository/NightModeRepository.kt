package com.example.playlistmaker.domain.repository

interface NightModeRepository {
    fun switchMode(isModeEnabled: Boolean)

    fun getSettingsValue(): Boolean

    fun setNightMode()
}