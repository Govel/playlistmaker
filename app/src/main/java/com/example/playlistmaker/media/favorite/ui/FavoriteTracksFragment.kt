package com.example.playlistmaker.media.favorite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.NavGraphDirections
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.search.ui.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavoriteTracksViewModel>()
    private var isClickAllowed = true
    private lateinit var adapter: TrackAdapter
    private lateinit var state: FavoriteState
    private val favoriteTrack = mutableListOf<Track>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.showFavoriteTracks()

        adapter = TrackAdapter(favoriteTrack) { clickedTrack ->
            if (clickDebounce()) {
                render(state)
                val action =
                    NavGraphDirections
                        .actionGlobalToAudioPlayerFragment(clickedTrack)
                findNavController().navigate(action)
            }
        }
        binding.rvFavoriteTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoriteTracks.adapter = adapter
        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
            state = it
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

    private fun showProgressBar() {
        binding.pbFavoriteTracks.isVisible = true
        binding.ivFavoriteEmpty.isVisible = false
        binding.tvFavoriteEmpty.isVisible = false
        binding.rvFavoriteTracks.isVisible = false
    }

    private fun showContent() {
        binding.pbFavoriteTracks.isVisible = false
        binding.ivFavoriteEmpty.isVisible = false
        binding.tvFavoriteEmpty.isVisible = false
        binding.rvFavoriteTracks.isVisible = true
    }

    private fun showEmpty() {
        binding.pbFavoriteTracks.isVisible = false
        binding.ivFavoriteEmpty.isVisible = true
        binding.tvFavoriteEmpty.isVisible = true
        binding.rvFavoriteTracks.isVisible = false
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Loading -> showProgressBar()
            is FavoriteState.Content -> {
                showContent()
                favoriteTrack.clear()
                favoriteTrack.addAll(state.tracks)
                adapter.notifyDataSetChanged()
            }

            is FavoriteState.Empty -> showEmpty()
        }
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}