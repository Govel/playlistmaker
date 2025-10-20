package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.repository.NightModeInteractor
import com.example.playlistmaker.domain.repository.NightModeRepository

class NightModeInteractorImpl(
    private val nightModeRepository: NightModeRepository
) : NightModeInteractor {
    override fun switchMode(isModeEnabled: Boolean) {
        nightModeRepository.switchMode(isModeEnabled)
    }

    override fun getSettingsValue() = nightModeRepository.getSettingsValue()

    override fun setNightMode() {
        nightModeRepository.setNightMode()
    }
}