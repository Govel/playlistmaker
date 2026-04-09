package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.root.ui.PlaylistMakerTheme
import com.example.playlistmaker.settings.domain.model.ThemeSettings
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {
    private val viewModel by viewModel<SettingsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val isCheckedState = viewModel.observeThemeSettingsLiveData()
                    .observeAsState(initial = ThemeSettings(false))
                PlaylistMakerTheme(darkTheme = isCheckedState.value.isChecked) {
                    Surface(color = MaterialTheme.colorScheme.primary) {
                        Settings(
                            isChecked = isCheckedState.value.isChecked,
                            themeSwitcher = viewModel::switchMode,
                            shareApp = {
                                viewModel.dispatchExternalNavigator(
                                    ExternalNavigatorState.Share
                                )
                            },
                            writeInSupport = {
                                viewModel.dispatchExternalNavigator(
                                    ExternalNavigatorState.Support
                                )
                            },
                            userAgreement = {
                                viewModel.dispatchExternalNavigator(
                                    ExternalNavigatorState.Terms
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}