package com.example.playlistmaker

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
            Toast.makeText(this@MainActivity, "Кнопка поиска", Toast.LENGTH_SHORT).show()
        }
        val buttonSettings = findViewById<Button>(R.id.settings_button)
        val buttonSettingsClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Кнопка настроек", Toast.LENGTH_SHORT).show()
            }
        }
        buttonSettings.setOnClickListener(buttonSettingsClickListener)
        val buttonMedia = findViewById<Button>(R.id.media_button)
        buttonMedia.setOnClickListener {
            Toast.makeText(this@MainActivity, "Кнопка медиа", Toast.LENGTH_SHORT).show()
        }
    }
}

