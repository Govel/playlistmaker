package com.example.playlistmaker.di

import com.example.playlistmaker.db.data.repository.FavoriteTrackRepositoryImpl
import com.example.playlistmaker.db.domain.api.FavoriteTrackRepository
import com.example.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.repository.TracksSearchRepositoryImpl
import com.example.playlistmaker.search.data.storages.local.SharedPrefsClient
import com.example.playlistmaker.search.domain.repository.HistoryRepository
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.repository.SettingsRepository
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator
import org.koin.dsl.module

val repositoryModule = module {
    single<HistoryRepository> {
        HistoryRepositoryImpl(get<SharedPrefsClient<String>>(HISTORY_PREFS), get())
    }
    single<TracksRepository> {
        TracksSearchRepositoryImpl(get())
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(get<SharedPrefsClient<Boolean>>(NIGHT_PREFS), get())
    }
    single<ExternalNavigator> {
        ExternalNavigatorImpl(get())
    }
    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
    }
}