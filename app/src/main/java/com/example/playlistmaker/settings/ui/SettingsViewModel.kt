package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import com.example.playlistmaker.settings.domain.repository.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel(){

    private val themeSettingsLiveData = MutableLiveData<ThemeSettings>()
    fun observeThemeSettingsLiveData(): LiveData<ThemeSettings> = themeSettingsLiveData
    init {
        getThemeSettings()
    }
    fun switchMode(isChecked: Boolean) {
        themeSettingsLiveData.value = ThemeSettings(isChecked = isChecked)
        settingsInteractor.updateThemeSettings(ThemeSettings(isChecked))
    }

    fun getThemeSettings(){
        val currentThemeSettings = getCurrentTheme()
        themeSettingsLiveData.value = ThemeSettings(isChecked = currentThemeSettings)
    }

    fun getCurrentTheme(): Boolean = settingsInteractor.getThemeSettings().isChecked

    fun dispatchExternalNavigator(state: ExternalNavigatorState) {
        when(state) {
            is ExternalNavigatorState.Share -> sharingInteractor.shareApp()
            is ExternalNavigatorState.Support -> sharingInteractor.openSupport()
            is ExternalNavigatorState.Terms -> sharingInteractor.openTerms()
        }
    }

    companion object {
        fun getFactory() = viewModelFactory {
            initializer {
                val provideSharingInteractor = Creator.provideSharingInteractor()
                val provideSettingsInteractor = Creator.provideSettingsInteractor()
                SettingsViewModel(provideSharingInteractor, provideSettingsInteractor)
            }
        }
    }
}