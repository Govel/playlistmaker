package com.example.playlistmaker.sharing.domain.repository

interface ExternalNavigator {
    fun shareLink()
    fun openLink()
    fun openEmail()
}