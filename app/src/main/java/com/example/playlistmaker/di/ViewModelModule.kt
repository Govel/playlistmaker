package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.FavoriteTracksViewModel
import com.example.playlistmaker.media.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.AudioPlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val viewModelModule = module {
    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }
    viewModel { (url: String) ->
        AudioPlayerViewModel(url, get())
    }
    viewModel {
        FavoriteTracksViewModel(get())
    }
    viewModel {
        PlaylistsViewModel()
    }
}