package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.data.repository.tracks.HistoryRepositoryImpl
import com.example.playlistmaker.data.repository.audioplayer.MediaPlayerRepositoryImpl
import com.example.playlistmaker.data.repository.settings.NightModeRepositoryImpl
import com.example.playlistmaker.data.repository.tracks.TracksSearchRepositoryImpl
import com.example.playlistmaker.data.storages.local.SharedPrefsClient
import com.example.playlistmaker.data.storages.local.SharedPrefsHistoryTracks
import com.example.playlistmaker.data.storages.local.SharedPrefsNightMode
import com.example.playlistmaker.data.storages.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.impl.audioplayer.MediaPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.settings.NightModeInteractorImpl
import com.example.playlistmaker.domain.impl.settings.SettingsInteractorImpl
import com.example.playlistmaker.domain.repository.tracks.TracksInteractor
import com.example.playlistmaker.domain.impl.tracks.TracksInteractorImpl
import com.example.playlistmaker.domain.repository.tracks.HistoryRepository
import com.example.playlistmaker.domain.repository.audioplayer.MediaPlayerInteractor
import com.example.playlistmaker.domain.repository.audioplayer.MediaPlayerRepository
import com.example.playlistmaker.domain.repository.settings.NightModeRepository
import com.example.playlistmaker.domain.repository.settings.SettingsInteractor
import com.google.gson.Gson

object Creator {

    private lateinit var application: Application
    private val gson = Gson()

    fun initApplication(application: Application) {
        this.application = application
    }
    private lateinit var keyNightMode: String

    fun setKeyNightMode(key: String) {
        this.keyNightMode = key
    }

    private fun getAppContext() = application.applicationContext as App

    private fun provideSharedPreferences(key: String) =
        application.getSharedPreferences(key, Context.MODE_PRIVATE)

    private fun getNightModePrefsClient(key: String): SharedPrefsClient<Boolean> =
        SharedPrefsNightMode(provideSharedPreferences(key), key)

    private fun getNightModeRepository(key: String): NightModeRepository =
        NightModeRepositoryImpl(
            getNightModePrefsClient(key),
            getAppContext()
        )

    fun provideNightModeInteractor() = NightModeInteractorImpl(getNightModeRepository(keyNightMode))
    private fun getRetrofitNetworkClient(): RetrofitNetworkClient {
        return RetrofitNetworkClient()
    }
    private fun getTracksRepositoryImpl(): TracksSearchRepositoryImpl {
        return TracksSearchRepositoryImpl(getRetrofitNetworkClient())
    }
    private fun getSharedPrefsHistoryTracks(key: String): SharedPrefsClient<String> {
        return SharedPrefsHistoryTracks(provideSharedPreferences(key), key)
    }
    private fun getHistoryRepository(key: String): HistoryRepository {
        return HistoryRepositoryImpl(getSharedPrefsHistoryTracks(key), gson)
    }
    fun provideTracksInteractor(key: String): TracksInteractor {
        return TracksInteractorImpl(getTracksRepositoryImpl(), getHistoryRepository(key))
    }
    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(context)
    }
    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl(MediaPlayer())
    }
    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
    }
}
