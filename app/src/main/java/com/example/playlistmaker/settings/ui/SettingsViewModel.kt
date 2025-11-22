package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.repository.SettingsInteractor
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel(){


    companion object {
        fun getFactory() = viewModelFactory {
//            initializer {
//                val getExternalNavigator: ExternalNavigator
//                val provideSharingInteractor = Creator.provideSharingInteractor(getExternalNavigator)
//                SettingsViewModel(provideSharingInteractor, )
//            }
        }
    }
}