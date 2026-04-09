package com.example.playlistmaker.media.playlists.ui

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
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.media.playlists.new_playlist.ui.NewPlaylistFragmentDirections
import com.example.playlistmaker.media.playlists.playlist.ui.PlaylistFragmentDirections
import com.example.playlistmaker.media.playlists.ui.models.PlaylistsState
import com.example.playlistmaker.root.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val playlistsState = viewModel.observeStatePlaylists()
                    .observeAsState(initial = PlaylistsState.Empty)
                LaunchedEffect(Unit) {
                    viewModel.showPlaylists()
                }
                PlaylistMakerTheme() {
                    Surface(color = MaterialTheme.colorScheme.primary) {
                        Playlists(
                            state = playlistsState.value,
                            onClickNewPlaylist = {
                                val action =
                                    NewPlaylistFragmentDirections.actionGlobalToNewPlaylistFragment()
                                findNavController().navigate(action)
                            },
                            onPlaylistClick = { playlist ->
                                val action =
                                    PlaylistFragmentDirections.actionGlobalToPlaylistFragment(
                                        playlist
                                    )
                                findNavController().navigate(action)
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
