package com.example.playlistmaker.media.favorite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.NavGraphDirections
import com.example.playlistmaker.root.ui.PlaylistMakerTheme
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private val viewModel by viewModel<FavoriteTracksViewModel>()
    private var isClickAllowed: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val favoriteState = viewModel.observeState()
                    .observeAsState(initial = FavoriteState.Loading)
                LaunchedEffect(Unit) {
                    viewModel.showFavoriteTracks()
                }
                PlaylistMakerTheme {
                    Surface(color = MaterialTheme.colorScheme.primary) {
                        Favorite(
                            state = favoriteState.value,
                            onClick = { track ->
                                if (clickDebounce()) {
                                    val action =
                                        NavGraphDirections
                                            .actionGlobalToAudioPlayerFragment(track)
                                    findNavController().navigate(action)
                                }
                            })


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
}
