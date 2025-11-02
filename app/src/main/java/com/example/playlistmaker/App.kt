package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration

const val THEME_SWITCH_KEY = "theme_switch_key"
class App : Application() {
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
