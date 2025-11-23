package com.example.playlistmaker.search.data.storages.local

import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPrefsNightMode(
    private val sharedPreferences: SharedPreferences,
    private val key: String
) : SharedPrefsClient<Boolean> {
    override fun save(data: Boolean) {
        sharedPreferences.edit { putBoolean(key, data) }
    }

    override fun load(data: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, data)
    }

    override fun clear(data: Boolean) {
        return sharedPreferences.edit { remove(key) }
    }

}