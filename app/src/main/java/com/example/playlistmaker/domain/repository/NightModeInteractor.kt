package com.example.playlistmaker.domain.repository

interface NightModeInteractor {
    fun switchMode(isModeEnabled: Boolean)

    fun getSettingsValue(): Boolean

    fun setNightMode()
}