package com.example.playlistmaker.data.repository

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.data.storages.local.SharedPrefsClient
import com.example.playlistmaker.data.storages.local.SharedPrefsNightMode
import com.example.playlistmaker.domain.repository.NightModeRepository

class NightModeRepositoryImpl(
    private val nightModePrefsClient: SharedPrefsClient<Boolean>,
    private val appContext: Context
) : NightModeRepository {

    private fun isModeOn(): Boolean {
        val darkModeFlag =
            appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
    }

    override fun switchMode(isModeEnabled: Boolean) {
        nightModePrefsClient.save(
            isModeEnabled
        )
        setNightMode()
    }

    override fun getSettingsValue(): Boolean = nightModePrefsClient.load(
        isModeOn()
    )

    override fun setNightMode() {
        AppCompatDelegate.setDefaultNightMode(
            if (getSettingsValue()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}