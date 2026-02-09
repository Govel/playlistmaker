package com.example.playlistmaker.di

import com.example.playlistmaker.db.domain.api.FavoriteTrackInteractor
import com.example.playlistmaker.db.domain.api.PlaylistsInteractor
import com.example.playlistmaker.db.domain.impl.FavoriteTrackInteractorImpl
import com.example.playlistmaker.db.domain.impl.PlaylistsInteractorImpl
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalStorageInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.impl.ExternalStorageInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.repository.TracksInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.repository.SettingsInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.example.playlistmaker.sharing.domain.repository.SharingInteractor
import org.koin.dsl.module

val interactorModule = module {
    factory<TracksInteractor> {
        TracksInteractorImpl(get(), get())
    }
    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }
    factory<FavoriteTrackInteractor> {
        FavoriteTrackInteractorImpl(get())
    }
    factory<PlaylistsInteractor> {
        PlaylistsInteractorImpl(get())
    }
    factory<ExternalStorageInteractor> {
        ExternalStorageInteractorImpl(get())
    }
}