package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonSearch = findViewById<Button>(R.id.search_button)
        buttonSearch.setOnClickListener {
            val displaySearch = Intent(this, SearchActivity::class.java)
            startActivity(displaySearch)
        }
        val buttonSettings = findViewById<Button>(R.id.settings_button)
        val buttonSettingsClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View) {
                val displaySettings = Intent(v.context, SettingsActivity::class.java)
                startActivity(displaySettings)
            }
        }
        buttonSettings.setOnClickListener(buttonSettingsClickListener)
        val buttonMedia = findViewById<Button>(R.id.media_button)
        buttonMedia.setOnClickListener {
            val displayMedia = Intent(this, MediaActivity::class.java)
            startActivity(displayMedia)
        }
    }
}

