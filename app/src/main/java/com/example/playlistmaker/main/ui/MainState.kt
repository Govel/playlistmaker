package com.example.playlistmaker.main.ui

sealed interface MainState {
    object DispatchSearch: MainState
    object DispatchMedia: MainState
    object DispatchSettings: MainState
}