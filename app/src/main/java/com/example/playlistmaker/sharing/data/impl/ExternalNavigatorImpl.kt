package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.sharing.data.model.EmailData
import com.example.playlistmaker.sharing.domain.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator{
    override fun shareLink(shareAppLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareAppLink)
        Intent.createChooser(shareIntent, null)
        context.startActivity(Intent.createChooser(shareIntent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openLink(termLink: String) {

        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(termLink)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openEmail(supportEmailData: EmailData) {
        val writeInSupportIntent = Intent(Intent.ACTION_SENDTO)
        writeInSupportIntent.data = Uri.parse("mailto:")
        writeInSupportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.recipientEmail))
        writeInSupportIntent.putExtra(Intent.EXTRA_SUBJECT, supportEmailData.emailSubject)
        writeInSupportIntent.putExtra(Intent.EXTRA_TEXT, supportEmailData.emailBody)
        context.startActivity(writeInSupportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}