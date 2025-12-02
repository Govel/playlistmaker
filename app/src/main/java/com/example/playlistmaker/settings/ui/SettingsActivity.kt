//package com.example.playlistmaker.settings.ui
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.playlistmaker.R
//import com.example.playlistmaker.databinding.ActivitySettingsBinding
//import org.koin.androidx.viewmodel.ext.android.viewModel
//
//class SettingsActivity : AppCompatActivity() {
//    private lateinit var binding: ActivitySettingsBinding
//    private val viewModel by viewModel<SettingsViewModel>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySettingsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        binding.mtbArrowback.setNavigationOnClickListener {
//            finish()
//        }
//        binding.share.setOnClickListener {
//            viewModel.dispatchExternalNavigator(
//                ExternalNavigatorState.Share
//            )
//        }
//        binding.writeInSupport.setOnClickListener {
//            viewModel.dispatchExternalNavigator(
//                ExternalNavigatorState.Support
//            )
//        }
//        binding.userAgreement.setOnClickListener {
//            viewModel.dispatchExternalNavigator(
//                ExternalNavigatorState.Terms
//            )
//        }
//        viewModel.observeThemeSettingsLiveData().observe(this) {
//            setThemeSwitch(it.isChecked)
//        }
//        binding.themeSwitcher.setOnClickListener { viewModel.switchMode(binding.themeSwitcher.isChecked) }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        setThemeSwitch(viewModel.getCurrentTheme())
//    }
//
//    fun setThemeSwitch(isChecked: Boolean) {
//        binding.themeSwitcher.isChecked = isChecked
//    }
//}