package com.example.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.example.playlistmaker.ui.tracks.SearchHistory


const val THEME_SWITCH_KEY = "theme_switch_key"
class App : Application() {
    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(SearchHistory.SHARED_PREFERENСES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_SWITCH_KEY, false)
        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        if (darkTheme == darkThemeEnabled) return
        darkTheme = darkThemeEnabled
        getSharedPreferences(SearchHistory.SHARED_PREFERENСES, MODE_PRIVATE)
            .edit {
                putBoolean(THEME_SWITCH_KEY, darkThemeEnabled)
            }
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
