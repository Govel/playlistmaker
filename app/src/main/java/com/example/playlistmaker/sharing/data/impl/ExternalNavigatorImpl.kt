package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.model.EmailData
import com.example.playlistmaker.sharing.domain.repository.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator{
    override fun shareLink() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, getShareAppLink())
        Intent.createChooser(shareIntent, null)
        context.startActivity(Intent.createChooser(shareIntent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink() {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getTermsLink())).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openEmail() {
        val writeInSupportIntent = Intent(Intent.ACTION_SENDTO)
        writeInSupportIntent.data = Uri.parse("mailto:")
        writeInSupportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getSupportEmailData().recipientEmail))
        writeInSupportIntent.putExtra(Intent.EXTRA_SUBJECT, getSupportEmailData().emailSubject)
        writeInSupportIntent.putExtra(Intent.EXTRA_TEXT, getSupportEmailData().emailBody)
        context.startActivity(writeInSupportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun getShareAppLink(): String = context.getString(R.string.share_app_url)

    private fun getSupportEmailData(): EmailData = EmailData(
        recipientEmail = context.getString(R.string.e_mail),
        emailSubject = context.getString(R.string.email_subject),
        emailBody = context.getString(R.string.email_body)
    )

    private fun getTermsLink(): String = context.getString(R.string.user_agreement_url)
}