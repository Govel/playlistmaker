package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.mtbArrowback.setNavigationOnClickListener {
//            finish()
//        }
        binding.share.setOnClickListener {
            viewModel.dispatchExternalNavigator(
                ExternalNavigatorState.Share
            )
        }
        binding.writeInSupport.setOnClickListener {
            viewModel.dispatchExternalNavigator(
                ExternalNavigatorState.Support
            )
        }
        binding.userAgreement.setOnClickListener {
            viewModel.dispatchExternalNavigator(
                ExternalNavigatorState.Terms
            )
        }
        viewModel.observeThemeSettingsLiveData().observe(viewLifecycleOwner) {
            setThemeSwitch(it.isChecked)
        }
        binding.themeSwitcher.setOnClickListener { viewModel.switchMode(binding.themeSwitcher.isChecked) }
    }
    override fun onResume() {
        super.onResume()
        setThemeSwitch(viewModel.getCurrentTheme())
    }

    fun setThemeSwitch(isChecked: Boolean) {
        binding.themeSwitcher.isChecked = isChecked
    }
}