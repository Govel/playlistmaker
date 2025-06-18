package com.example.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val materialToolbar : MaterialToolbar = findViewById(R.id.title_settings)
        materialToolbar.setNavigationOnClickListener {
            finish()
        }
    }
}

