package com.example.playlistmaker.domain.impl

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.THEME_SWITCH_KEY
import com.example.playlistmaker.domain.repository.NightModeInteractor
import com.example.playlistmaker.domain.repository.SettingsInteractor
import com.example.playlistmaker.presentation.tracks.SearchHistory
import com.google.android.material.materialswitch.MaterialSwitch

class SettingsInteractorImpl(
    val context: Context
): SettingsInteractor {
    override fun getFrameShare() {
        val message = context.getString(R.string.share_app_url)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        val chooserIntent = Intent.createChooser(shareIntent, null)
        context.startActivity(chooserIntent)
    }

    override fun writeInSupport() {
        val recipientEmail = "s.gorlov123@yandex.ru"
        val emailSubject = context.getString(R.string.email_subject)
        val emailBody = context.getString(R.string.email_body)

        val writeInSupportIntent = Intent(Intent.ACTION_SENDTO)
        writeInSupportIntent.data = Uri.parse("mailto:")
        writeInSupportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
        writeInSupportIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
        writeInSupportIntent.putExtra(Intent.EXTRA_TEXT, emailBody)
        context.startActivity(writeInSupportIntent)
    }

    override fun getUserAgreement() {
        val userAgreementUrl = context.getString(R.string.user_agreement_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userAgreementUrl))
        context.startActivity(browserIntent)
    }



}