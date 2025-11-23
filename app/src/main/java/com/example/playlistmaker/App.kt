package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import com.example.playlistmaker.creator.Creator

const val THEME_SWITCH_KEY = "theme_switch_key"
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.initApplication(this)
        Creator.setKeyNightMode(THEME_SWITCH_KEY)
        val themeSwitcher = Creator.provideSettingsInteractor()
        themeSwitcher.switchTheme()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val themeManager = Creator.provideSettingsInteractor()
        themeManager.switchTheme()
    }
}
