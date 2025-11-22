package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.domain.repository.NightModeInteractor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var nightModeInteractor: NightModeInteractor
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var themeSwitcher: SwitchMaterial
//    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val materialToolbar: MaterialToolbar = findViewById(R.id.mtb_arrowback)
        materialToolbar.setNavigationOnClickListener {
            finish()
        }
        binding.share.setOnClickListener { getFrameShare() }
        binding.writeInSupport.setOnClickListener { writeInSupport() }
        binding.userAgreement.setOnClickListener { getUserAgreement() }
        nightModeInteractor = Creator.provideNightModeInteractor()
        themeSwitcher.setOnClickListener { nightModeInteractor.switchMode(binding.themeSwitcher.isChecked) }
    }

    override fun onResume() {
        super.onResume()
        binding.themeSwitcher.isChecked = nightModeInteractor.getSettingsValue()
    }

    fun getFrameShare() {
        val message = getString(R.string.share_app_url)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        val chooserIntent = Intent.createChooser(shareIntent, null)
        startActivity(chooserIntent)
    }

    fun writeInSupport() {
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

    fun getUserAgreement() {
        val userAgreementUrl = getString(R.string.user_agreement_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(userAgreementUrl))
        startActivity(browserIntent)
    }
}