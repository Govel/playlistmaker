package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.presentation.tracks.SearchHistory


const val THEME_SWITCH_KEY = "theme_switch_key"
class App : Application() {
    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        Creator.setKeyNightMode(THEME_SWITCH_KEY)
        val themeSwitcher = Creator.provideNightModeInteractor()
        themeSwitcher.setNightMode()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val themeManager = Creator.provideNightModeInteractor()

        themeManager.setNightMode()
    }
}
