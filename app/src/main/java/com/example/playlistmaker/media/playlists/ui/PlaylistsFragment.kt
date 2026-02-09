package com.example.playlistmaker.media.playlists.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.playlists.new_playlist.domain.model.Playlist
import com.example.playlistmaker.media.playlists.new_playlist.ui.NewPlaylistFragmentDirections
import com.example.playlistmaker.media.playlists.ui.models.PlaylistsState
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistsViewModel>()

    private val playlists = mutableListOf<Playlist>()

    private lateinit var adapter: PlaylistsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showPlaylists()

        adapter = PlaylistsAdapter(playlists) { viewModel.getUriForCover(it) }

        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPlaylists.adapter = adapter

        viewModel.observeStatePlaylists().observe(viewLifecycleOwner) {
            render(it)
        }

        binding.btNewPlaylist.setOnClickListener {
            val action =
                NewPlaylistFragmentDirections.actionGlobalToNewPlaylistFragment()
            findNavController().navigate(action)
        }
    }

    private fun showContent() {
        binding.rvPlaylists.isVisible = true
        binding.ivEmpty.isVisible = false
        binding.tvEmpty.isVisible = false
    }

    private fun showEmpty() {
        binding.rvPlaylists.isVisible = false
        binding.ivEmpty.isVisible = true
        binding.tvEmpty.isVisible = true
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> {
                showContent()
                playlists.clear()
                playlists.addAll(state.playlists)
                adapter.notifyDataSetChanged()
            }

            is PlaylistsState.Empty -> showEmpty()
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}