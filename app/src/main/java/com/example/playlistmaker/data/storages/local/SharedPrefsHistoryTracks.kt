package com.example.playlistmaker.data.storages.local

import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPrefsHistoryTracks(
    private val sharedPreferences: SharedPreferences,
    private val key: String
) : SharedPrefsClient<String> {
    override fun save(data: String) {
        sharedPreferences.edit { putString(key, data) }
    }

    override fun load(data: String): String {
        return sharedPreferences.getString(key, data) ?: ""
    }

    override fun clear(data: String) {
        sharedPreferences.edit { remove(key) }
    }
}