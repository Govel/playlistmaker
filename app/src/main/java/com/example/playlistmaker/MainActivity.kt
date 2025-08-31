package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonSearch = findViewById<Button>(R.id.search_button)
        buttonSearch.setOnClickListener {
            val displaySearch = Intent(this, SearchActivity::class.java)
            startActivity(displaySearch)
        }
        val buttonSettings = findViewById<Button>(R.id.settings_button)
        buttonSettings.setOnClickListener {
            val displaySettings = Intent(this, SettingsActivity::class.java)
            startActivity(displaySettings)
        }
        val buttonMedia = findViewById<Button>(R.id.media_button)
        buttonMedia.setOnClickListener {
            val displayMedia = Intent(this, MediaActivity::class.java)
            startActivity(displayMedia)
        }
    }
}
