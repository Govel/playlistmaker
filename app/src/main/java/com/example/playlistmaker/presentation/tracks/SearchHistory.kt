package com.example.playlistmaker.presentation.tracks

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(
    private val prefs: SharedPreferences,
    private val gson: Gson = Gson(),
) {
    companion object {
        const val SHARED_PREFERENCES = "shared_prefs"
        private const val KEY_TRACKS = "tracks"
        private const val MAX_SIZE = 10
    }
    fun get(): MutableList<Track> {
        val json = prefs.getString(KEY_TRACKS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }
    fun clear() {
        prefs.edit().remove(KEY_TRACKS).apply()
    }
    fun setAll(list: List<Track>) {
        val json = gson.toJson(list)
        prefs.edit().putString(KEY_TRACKS, json).apply()
    }
    fun push(track: Track) {
        val list = get()
        list.removeAll { it.trackId == track.trackId }
        list.add(0, track)
        if (list.size > MAX_SIZE) {
            list.removeAt(list.size - 1)
        }
        setAll(list)
    }
}
