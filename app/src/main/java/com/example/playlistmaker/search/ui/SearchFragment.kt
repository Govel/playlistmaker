package com.example.playlistmaker.search.ui


import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.root.ui.PlaylistMakerTheme
import com.example.playlistmaker.search.ui.model.SearchState
import com.example.playlistmaker.util.NetworkCheckBroadcastReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private val viewModel by viewModel<SearchViewModel>()
    private val networkCheckBroadcastReceiver = NetworkCheckBroadcastReceiver()
    private var receiverRegistered = false

    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val searchState = viewModel.observeStateSearch()
                    .observeAsState(initial = SearchState.StandBy)
                var textFieldText by remember { mutableStateOf(TEXT_DEF) }
                val historyTracks by viewModel.historyTracks.collectAsState(initial = emptyList())
                LaunchedEffect(textFieldText) {
                    viewModel.searchDebounce(textFieldText)
                }
                PlaylistMakerTheme() {
                    Surface(color = MaterialTheme.colorScheme.primary) {
                        Search(
                            historyTracks = historyTracks,
                            state = searchState.value,
                            textFieldText = textFieldText,
                            searchTextField = { newText -> textFieldText = newText },
                            updateResult = {
                                viewModel.searchImmediately(textFieldText)
                            },
                            onTrackClick = { track ->
                                if (clickDebounce()) {
                                    viewModel.saveTrackToHistory(track)
                                    val action =
                                        SearchFragmentDirections.actionSearchFragmentToAudioPlayerFragment(
                                            track
                                        )
                                    findNavController().navigate(action)
                                }
                            },
                            clearHistory = {
                                viewModel.clearHistory()
                            }
                        )
                    }
                }
            }
        }
    }

        private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    override fun onResume() {
        super.onResume()
        checkConnection()
    }

    private fun checkConnection() {
        if (!receiverRegistered) {
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireContext().registerReceiver(
                    networkCheckBroadcastReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
                )
            } else {
                @Suppress("DEPRECATION")
                requireContext().registerReceiver(networkCheckBroadcastReceiver, filter)
            }

            receiverRegistered = true
        }
    }

    override fun onPause() {
        requireContext().unregisterReceiver(networkCheckBroadcastReceiver)
        receiverRegistered = false
        super.onPause()
    }

    companion object {
        const val TEXT_DEF = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}