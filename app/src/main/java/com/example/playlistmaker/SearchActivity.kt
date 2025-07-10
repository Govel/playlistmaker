package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val materialToolbar: MaterialToolbar = findViewById(R.id.title_search)
        val clearButton = findViewById<ImageView>(R.id.search_clearIcon)
        val container = findViewById<LinearLayout>(R.id.search_container)
        val inputEditText = findViewById<EditText>(R.id.search_bar)
        materialToolbar.setNavigationOnClickListener {
            finish()
        }
        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empry
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisability(s)
            }

            override fun afterTextChanged(s: Editable?) {
                //empry
            }
        }
        inputEditText.addTextChangedListener(searchTextWatcher)
        clearButton.setOnClickListener {
            inputEditText.setText("");
        }
    }

    private fun clearButtonVisability(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

}