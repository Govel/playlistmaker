package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksSearchRepositoryImpl
import com.example.playlistmaker.search.data.storages.local.SharedPrefsClient
import com.example.playlistmaker.search.data.storages.local.SharedPrefsHistoryTracks
import com.example.playlistmaker.search.data.storages.local.SharedPrefsNightMode
import com.example.playlistmaker.search.data.storages.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksInteractor
import com.example.playlistmaker.settings.data.SettingsRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.repository.SettingsInteractor
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
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

    private fun getRetrofitNetworkClient(): RetrofitNetworkClient {
        return RetrofitNetworkClient(getAppContext())
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
    private fun getExternalNavigator(): ExternalNavigator {
        return ExternalNavigatorImpl(getAppContext())
    }
    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(), getAppContext())
    }

    private fun getSettingsRepository(key: String): SettingsRepository {
        return SettingsRepositoryImpl(
            getNightModePrefsClient(key),
            getAppContext()
        )
    }
    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(keyNightMode))
    }

}