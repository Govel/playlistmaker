package com.example.playlistmaker.media.playlists.playlist.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.media.playlists.playlist.ui.models.TracksState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.SearchFragment.Companion.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.util.LocalUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf

class PlaylistFragment : Fragment() {
    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val args: PlaylistFragmentArgs by navArgs()
    private val playlist: Playlist get() = args.currentPlaylist
    private lateinit var viewModel: PlaylistViewModel
    private var isClickAllowed = true
    private lateinit var adapter: PlaylistTrackAdapter
    private val tracks = mutableListOf<Track>()
    private lateinit var dialog: MaterialAlertDialogBuilder
    private lateinit var bottomSheetMenuBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var shouldRestoreBottomSheet = false
    private var isCurrentStateEmpty = false
    private var currentPlaylist: Playlist? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (shouldRestoreBottomSheet) {
            shouldRestoreBottomSheet = false
            if (bottomSheetMenuBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            binding.overlay.isVisible = true
        }
        viewModel.loadPlaylist(args.currentPlaylist.id)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mtbArrowback.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel = getKoin().get {
            parametersOf(playlist.tracksList)
        }
        val fullPath = viewModel.getFullPath(playlist.imgPath)

        bottomSheetMenuBehavior = BottomSheetBehavior.from(binding.standardBottomSheetMenu)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)
        bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        Glide.with(binding.root)
            .load(fullPath)
            .placeholder(R.drawable.placeholder_cover)
            .transform(
                CenterCrop(),
                RoundedCorners(LocalUtils().dpToPx(2f, binding.ivPlaylistCoverBottom))
            )
            .into(binding.ivPlaylistCoverBottom)

        binding.apply {
            tvTitle.text = playlist.name
            tvTotalTracks.text = playlist.totalTracks.toString() + formatTrack(playlist.totalTracks)
        }

        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
            .setTitle(getString(R.string.new_playlist_exit_dialog_name))
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                findNavController().navigateUp()
            }

        viewModel.getTracks()

        viewModel.observeStateTracks().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            currentPlaylist = playlist

            binding.tvTitle.text = playlist.name
            binding.tvPlaylistName.text = playlist.name
            binding.tvPlaylistDescription.text = playlist.description

            val count = playlist.totalTracks
            binding.tvTotalTracks.text = count.toString() + formatTrack(count)

            val coverUri = viewModel.getFullPath(playlist.imgPath)
            Glide.with(binding.root)
                .load(coverUri)
                .placeholder(R.drawable.placeholder_cover)
                .transform(CenterCrop(), RoundedCorners(LocalUtils().dpToPx(2f, binding.ivPlaylistCoverBottom)))
                .into(binding.ivPlaylistCoverBottom)

            Glide.with(binding.root)
                .load(coverUri)
                .placeholder(R.drawable.placeholder_cover)
                .transform(CenterCrop())
                .into(binding.ivPlaylistCover)
        }

        binding.rvTracks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = PlaylistTrackAdapter(
            tracks = tracks,
            onItemLongClick = { clickedTrack ->
                showDeleteTrackDialog(clickedTrack)
                true
            },
            onItemClick = { clickedTrack ->
                if (clickDebounce()) {
                    val action =
                        PlaylistFragmentDirections.actionGlobalToAudioPlayerFragment(clickedTrack)
                    findNavController().navigate(action)
                }
            }
        )

        binding.rvTracks.adapter = adapter

        Glide.with(binding.root)
            .load(fullPath)
            .placeholder(R.drawable.placeholder_cover)
            .transform(CenterCrop())
            .into(binding.ivPlaylistCover)
        binding.apply {
            tvPlaylistName.text = playlist.name
            tvPlaylistDescription.text = playlist.description
        }

        bottomSheetMenuBehavior.apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetMenuBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                        showTracks()
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        bottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.btMenu.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                showMenu()
            } else if (bottomSheetMenuBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                showTracks()
            }
        }

        binding.btShare.setOnClickListener {
            sharePlaylist()
        }

        binding.btShareMenu.setOnClickListener {
            sharePlaylist()
        }
        binding.btDeleteMenu.setOnClickListener {
            showDeletePlaylistDialog()
        }
        binding.btEditMenu.setOnClickListener {
            val action =
                PlaylistFragmentDirections.actionGlobalToEditPlaylistFragment(playlist.id)
            findNavController().navigate(action)
        }

    }

    private fun sharePlaylist() {
        if (isCurrentStateEmpty) {
            showEmptyToast()
        } else {
            viewModel.sharePlaylist(currentPlaylist, tracks)
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

    private fun showContent() {
        binding.apply {
            rvTracks.isVisible = true
        }
    }

    private fun showMenu() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun showTracks() {
        bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }


    @SuppressLint("SetTextI18n")
    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Content -> {
                showContent()
                tracks.clear()
                tracks.addAll(state.tracks)
                adapter.notifyDataSetChanged()
                val count = state.tracks.size
                val totalTime = viewModel.getTotalTime(state.tracks)
                binding.apply {
                    tvTotalTracksCount.text = "$count${formatTrack(count)}"
                    tvTotalTracksTime.text = "$totalTime${formatTrackMinute(totalTime)}"
                    tvEmptyTrackInPlaylist.isVisible = false
                }

                isCurrentStateEmpty = false
            }

            else -> {
                tracks.clear()
                adapter.notifyDataSetChanged()
                binding.apply {
                    tvTotalTracksCount.text = "0${formatTrack(0)}"
                    tvTotalTracksTime.text = "0${formatTrackMinute("0")}"
                    isCurrentStateEmpty = true
                    tvEmptyTrackInPlaylist.isVisible = true
                }
            }
        }
    }

    private fun formatTrack(count: Int): String {
        return when (count % 10) {
            0, 5, 6, 7, 8, 9 -> " треков"
            2, 3, 4 -> " трека"
            else -> " трек"
        }
    }

    private fun formatTrackMinute(count: String): String {
        return when (count.toInt() % 10) {
            0, 5, 6, 7, 8, 9 -> " минут"
            2, 3, 4 -> " минуты"
            else -> " минута"
        }
    }

    private fun showDeleteTrackDialog(track: Track) {
        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
            .setTitle(getString(R.string.delete_track_into_playlist))
            .setMessage("")
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteTrack(playlist, track)
            }
        dialog.show()
    }

    private fun showEmptyToast() {
        Toast.makeText(requireContext(),getString(R.string.empty_tracks_into_playlist), Toast.LENGTH_SHORT).show()
    }

    private fun showDeletePlaylistDialog() {
        val text = "${getString(R.string.want_delete_playlist)} \"${playlist.name}\"?"
        dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogTheme)
            .setTitle(text)
            .setMessage("")
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                findNavController().navigateUp()
                viewModel.deletePlaylist(playlist)
            }
        dialog.show()
    }
}
