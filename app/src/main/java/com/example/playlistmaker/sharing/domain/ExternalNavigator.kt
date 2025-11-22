package com.example.playlistmaker.sharing.domain

import android.content.Intent
import com.example.playlistmaker.sharing.data.model.EmailData

interface ExternalNavigator {
    fun shareLink(shareAppLink: String)
    fun openLink(termLink: String)
    fun openEmail(supportEmailData: EmailData)
}