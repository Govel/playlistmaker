package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.media.playlists.new_playlist.ui.NewPlaylistFragmentDirections
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.player.ui.models.TrackOnPlaylistState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.LocalUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val args: AudioPlayerFragmentArgs by navArgs()
    private val track: Track get() = args.currentTrack
    private lateinit var currentTrack: Track

    private val playlists = mutableListOf<Playlist>()

    private lateinit var adapter: BsPlaylistsAdapter

    private lateinit var viewModel: AudioPlayerViewModel
    private var shouldRestoreBottomSheet = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mtbArrowback.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        currentTrack = track
        viewModel = getKoin().get {
            parametersOf(currentTrack.previewUrl)
        }
        Glide.with(binding.main.context).load(
            currentTrack.getCoverArtwork()
        ).placeholder(R.drawable.placeholder_cover).fitCenter().transform(
            RoundedCorners(
                LocalUtils().dpToPx(8.0f, binding.main)
            )
        ).into(binding.ivArtwork)

        binding.tvTrackName.text = currentTrack.trackName
        binding.tvArtistName.text = currentTrack.artistName
        binding.tvTrackTimeMillis.text = LocalUtils().dateFormat(currentTrack.trackTimeMillis)
        binding.tvTrackTime.text = getString(R.string.timer)
        if (currentTrack.collectionName.isNullOrEmpty()) {
            binding.groupAlbum.isVisible = false
        } else {
            binding.groupAlbum.isVisible = true
            binding.tvCollectionTrackName.text = currentTrack.collectionName
        }
        if (currentTrack.releaseDate.isNullOrEmpty()) {
            binding.groupYear.isVisible = false
        } else {
            binding.groupYear.isVisible = true
            binding.tvReleaseTrackDate.text = currentTrack.getYearTrack()
        }
        binding.tvPrimaryGenreTrackName.text = currentTrack.primaryGenreName ?: ""
        binding.tvTrackCountry.text = currentTrack.country ?: ""
        viewModel.observePlayerState().observe(viewLifecycleOwner) {
            render(it)
            setImageButtonPlay(it.buttonText)
            enableButton(it.isPlayButtonEnabled)
            binding.tvTrackTime.text = it.progress
        }

        viewModel.showPlaylists()

        val bottomSheetContainer = binding.standardBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        viewModel.observeIsInPlaylist().observe(viewLifecycleOwner) {
            var message: String
            if (it.second) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                message = getString(R.string.track_added_to_playlist) + " \"${it.first}\""
            } else {
                message = getString(R.string.track_in_playlist)
            }

            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        adapter = BsPlaylistsAdapter(playlists, {coverName -> viewModel.getUriForCover(coverName)}
        ) { clickedPlaylist ->
            viewModel.isOnPlaylist(clickedPlaylist, currentTrack)
        }

        binding.rvBsPlaylists.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvBsPlaylists.adapter = adapter

        viewModel.observeTrackOnPlaylistState().observe(viewLifecycleOwner) {
            renderPlaylists(it)
        }

        viewModel.observeIsFavoriteTrack().observe(viewLifecycleOwner) {
            setImageButtonFavorite(it.isFavorite)
        }

        viewModel.checkInitialFavoriteState(currentTrack.trackId)

        binding.btPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        binding.btAddFavorite.setOnClickListener {
            viewModel.onFavoriteClicked(currentTrack)
        }

        binding.btAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.btNewPlaylist.setOnClickListener {
            shouldRestoreBottomSheet = true
            val action =
                NewPlaylistFragmentDirections.actionGlobalToNewPlaylistFragment()
            findNavController().navigate(action)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (shouldRestoreBottomSheet) {
            shouldRestoreBottomSheet = false
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            binding.overlay.isVisible = true
        }
    }

    private fun enableButton(isEnabled: Boolean) {
        binding.btPlay.isEnabled = isEnabled
    }

    private fun setImageButtonPlay(buttonText: String) {
        binding.btPlay.setImageResource(if (buttonText == "PAUSE") R.drawable.pause else R.drawable.play)
    }

    private fun setImageButtonFavorite(isFavorite: Boolean) {
        binding.btAddFavorite.setImageResource(if (isFavorite) R.drawable.ic_is_favorite else R.drawable.favorite)
    }

    fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> showProgressBar()
            else -> showContent()
        }
    }

    fun renderPlaylists(state: TrackOnPlaylistState) {
        when (state) {
            is TrackOnPlaylistState.Content -> {
                showContentPlaylists()
                playlists.clear()
                playlists.addAll(state.playlists)
                adapter.notifyDataSetChanged()
            }

            else -> showEmptyPlaylists()
        }
    }

    private fun showContentPlaylists() {
        binding.rvBsPlaylists.isVisible = true
        binding.ivEmpty.isVisible = false
        binding.tvEmpty.isVisible = false
    }

    private fun showEmptyPlaylists() {
        binding.rvBsPlaylists.isVisible = false
        binding.ivEmpty.isVisible = true
        binding.tvEmpty.isVisible = true
    }

    private fun showProgressBar() {
        binding.svPlayer.isVisible = false
        binding.pbPlayer.isVisible = true
    }

    private fun showContent() {
        binding.svPlayer.isVisible = true
        binding.pbPlayer.isVisible = false
    }

}