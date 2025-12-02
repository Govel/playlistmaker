//package com.example.playlistmaker.main.ui
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.playlistmaker.databinding.ActivityMainBinding
//import com.example.playlistmaker.media.ui.MediaActivity
//import com.example.playlistmaker.settings.ui.SettingsActivity
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class MainActivity : AppCompatActivity() {
//    private lateinit var binding: ActivityMainBinding
//    private val viewModel by viewModel<MainViewModel>()
//
//    @SuppressLint("ServiceCast")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//        viewModel.observeDispatcher().observe(this) {
//            dispatcher(it)
//        }
//        binding.searchButton.setOnClickListener {
//            viewModel.dispatchSearch()
//        }
//        binding.settingsButton.setOnClickListener {
//            viewModel.dispatchMedia()
//        }
//        binding.mediaButton.setOnClickListener {
//            viewModel.dispatchSettings()
//        }
//    }
//
//    private fun dispatcher(state: MainState) {
//        val intent = when (state) {
//            is MainState.DispatchSearch -> Intent(this, SearchActivity::class.java)
//            is MainState.DispatchMedia -> Intent(this, SettingsActivity::class.java)
//            is MainState.DispatchSettings -> Intent(this, MediaActivity::class.java)
//        }
//        startActivity(intent)
//    }
//}