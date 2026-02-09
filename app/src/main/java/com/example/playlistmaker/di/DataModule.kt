package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.data.converter.FavoriteTrackDbConverter
import com.example.playlistmaker.db.data.converter.PlaylistDbConverter
import com.example.playlistmaker.db.data.converter.TrackIntoPlaylistsDbConverter
import com.example.playlistmaker.search.data.storages.local.SharedPrefsClient
import com.example.playlistmaker.search.data.storages.local.SharedPrefsHistoryTracks
import com.example.playlistmaker.search.data.storages.local.SharedPrefsNightMode
import com.example.playlistmaker.search.data.storages.network.ITunesApi
import com.example.playlistmaker.search.data.storages.network.NetworkClient
import com.example.playlistmaker.search.data.storages.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val I_TUNES_BASE_URL = "https://itunes.apple.com"
private const val SHARED_PREFERENCES = "shared_prefs"
private const val NIGHT_MODE_KEY = "night_mode"
val HISTORY_PREFS = named("history_prefs")
val NIGHT_PREFS = named("night_prefs")

val dataModule = module {
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl(I_TUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    factory { Gson() }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db").build()
    }

    single {
        get<AppDatabase>().favoriteTrackDao()
    }

    single {
        get<AppDatabase>().playlistsDao()
    }

    single {
        get<AppDatabase>().trackIntoPlaylistsDao()
    }

    single { FavoriteTrackDbConverter() }

    single { PlaylistDbConverter() }

    single { TrackIntoPlaylistsDbConverter() }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single<SharedPrefsClient<String>>(HISTORY_PREFS) {
        SharedPrefsHistoryTracks(androidContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE), SHARED_PREFERENCES)
    }
    single<SharedPrefsClient<Boolean>>(NIGHT_PREFS) {
        SharedPrefsNightMode(androidContext().getSharedPreferences(NIGHT_MODE_KEY, Context.MODE_PRIVATE), NIGHT_MODE_KEY)
    }
}