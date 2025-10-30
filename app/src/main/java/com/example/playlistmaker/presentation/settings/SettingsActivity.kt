package com.example.playlistmaker.presentation.settings

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.repository.NightModeInteractor
import com.example.playlistmaker.domain.repository.SettingsInteractor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsInteractor: SettingsInteractor
    private lateinit var nightModeInteractor: NightModeInteractor

    private lateinit var themeSwitcher: SwitchMaterial
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        settingsInteractor = Creator.provideSettingsInteractor(this)
        val materialToolbar : MaterialToolbar = findViewById(R.id.mtb_arrowback)
        materialToolbar.setNavigationOnClickListener {
            finish()
        }
        val frameShare = findViewById<FrameLayout>(R.id.share)
        frameShare.setOnClickListener {
            settingsInteractor.getFrameShare()

        }
        val writeInSupport = findViewById<FrameLayout>(R.id.write_in_support)
        writeInSupport.setOnClickListener {
            settingsInteractor.writeInSupport()
        }
        val userAgreement = findViewById<FrameLayout>(R.id.user_agreement)
        userAgreement.setOnClickListener {
            settingsInteractor.getUserAgreement()
        }
        themeSwitcher = findViewById(R.id.themeSwitcher)
        nightModeInteractor = Creator.provideNightModeInteractor()
        themeSwitcher.setOnClickListener { nightModeInteractor.switchMode(themeSwitcher.isChecked) }
    }

    override fun onResume() {
        super.onResume()
        themeSwitcher.isChecked = nightModeInteractor.getSettingsValue()
    }
}
