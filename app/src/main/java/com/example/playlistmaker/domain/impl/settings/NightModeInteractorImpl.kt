package com.example.playlistmaker.domain.impl.settings

import com.example.playlistmaker.domain.repository.settings.NightModeInteractor
import com.example.playlistmaker.domain.repository.settings.NightModeRepository

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
