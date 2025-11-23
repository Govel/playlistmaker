package com.example.playlistmaker.settings.ui

sealed interface ExternalNavigatorState {
    object Share : ExternalNavigatorState

    object Support : ExternalNavigatorState

    object Terms : ExternalNavigatorState
}