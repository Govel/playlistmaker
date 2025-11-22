package com.example.playlistmaker.sharing.domain


import android.content.Context
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val context: Context
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

    private fun getShareAppLink(): String = context.getString(R.string.share_app_url)

    private fun getSupportEmailData(): EmailData = EmailData(
        recipientEmail = "s.gorlov123@gmail.com",
        emailSubject = context.getString(R.string.email_subject),
        emailBody = context.getString(R.string.email_body)
    )

    private fun getTermsLink(): String = context.getString(R.string.user_agreement_url)
}