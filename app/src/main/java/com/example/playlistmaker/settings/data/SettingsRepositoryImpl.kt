package com.example.playlistmaker.settings.data

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.search.data.storages.local.SharedPrefsClient
import com.example.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(
    private val nightModePrefsClient: SharedPrefsClient<Boolean>,
    private val appContext: Context
): SettingsRepository {
    private fun isModeOn(): Boolean {
        val darkModeFlag =
            appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
    }
    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(nightModePrefsClient.load(isModeOn()))
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        nightModePrefsClient.save(
            settings.isChecked
        )
        switchTheme()
    }
    override fun switchTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (getThemeSettings().isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}