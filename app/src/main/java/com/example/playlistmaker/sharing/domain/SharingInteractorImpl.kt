package com.example.playlistmaker.sharing.domain

import com.example.playlistmaker.sharing.data.model.EmailData
import com.example.playlistmaker.sharing.data.ExternalNavigator

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        TODO()
    }

    private fun getSupportEmailData(): EmailData {
        TODO()
    }

    private fun getTermsLink(): String {
        TODO()
    }
}