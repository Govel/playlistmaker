package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.repository.NightModeInteractor
import com.example.playlistmaker.settings.domain.repository.NightModeRepository

class NightModeInteractorImpl(
    private val nightModeRepository: NightModeRepository
) : NightModeInteractor {
    override fun switchMode(isModeEnabled: Boolean) {
        nightModeRepository.switchMode(isModeEnabled)
    }

    override fun getSettingsValue(): Boolean {
        return nightModeRepository.getSettingsValue()
    }

    override fun setNightMode() {
        nightModeRepository.setNightMode()
    }
}