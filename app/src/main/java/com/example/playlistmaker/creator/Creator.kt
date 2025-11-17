package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.App
import com.example.playlistmaker.player.data.repository.MediaPlayerRepositoryImpl
import com.example.playlistmaker.settings.data.NightModeRepositoryImpl
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksSearchRepositoryImpl
import com.example.playlistmaker.search.data.storages.local.SharedPrefsClient
import com.example.playlistmaker.search.data.storages.local.SharedPrefsHistoryTracks
import com.example.playlistmaker.search.data.storages.local.SharedPrefsNightMode
import com.example.playlistmaker.search.data.storages.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.example.playlistmaker.settings.domain.impl.NightModeInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.player.domain.repository.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.example.playlistmaker.settings.domain.repository.NightModeRepository
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksInteractor
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
    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl(MediaPlayer())
    }
    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
    }
}