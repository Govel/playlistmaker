package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val materialToolbar : MaterialToolbar = findViewById(R.id.title_settings)
        materialToolbar.setNavigationOnClickListener {
            finish()
        }
        val frameShare = findViewById<FrameLayout>(R.id.share)
        frameShare.setOnClickListener {
            val message = getString(R.string.share_app_url)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            val chooserIntent = Intent.createChooser(shareIntent, null)
            startActivity(chooserIntent)

        }
        val writeInSupport = findViewById<FrameLayout>(R.id.write_in_support)
        writeInSupport.setOnClickListener {
            val recipientEmail = "s.gorlov123@yandex.ru"
            val emailSubject = getString(R.string.email_subject)
            val emailBody = getString(R.string.email_body)

            val writeInSupportIntent = Intent(Intent.ACTION_SENDTO)
            writeInSupportIntent.data = Uri.parse("mailto:")
            writeInSupportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
            writeInSupportIntent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            writeInSupportIntent.putExtra(Intent.EXTRA_TEXT, emailBody)
            startActivity(writeInSupportIntent)
        }
        val userAgreement = findViewById<FrameLayout>(R.id.user_agreement)
        userAgreement.setOnClickListener {
            val userAgreementUrl = getString(R.string.user_agreement_url)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userAgreementUrl))
            startActivity(browserIntent)
        }
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val isDarkTheme = (applicationContext as App).darkTheme
        themeSwitcher.isChecked = isDarkTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
            val sharedPrefs = getSharedPreferences(SearchHistory.SHARED_PREFERENСES, MODE_PRIVATE)
            sharedPrefs.edit()
                .putBoolean(THEME_SWITCH_KEY, checked)
                .apply()
        }
    }
}
