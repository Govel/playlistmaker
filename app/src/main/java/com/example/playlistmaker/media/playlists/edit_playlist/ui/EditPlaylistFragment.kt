package com.example.playlistmaker.media.playlists.edit_playlist.ui


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.media.playlists.new_playlist.ui.NewPlaylistFragment
import com.example.playlistmaker.util.LocalUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class EditPlaylistFragment: NewPlaylistFragment() {
    override val viewModel: EditPlaylistViewModel by viewModel() {
        val playlistId = requireArguments().getInt("playlistId")
        parametersOf(playlistId)
    }

    private lateinit var currentPlaylist: Playlist


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = requireArguments().getInt("playlistId")
        viewModel.init(playlistId)

        binding.btCreate.text = getString(R.string.save)
        binding.mtbArrowback.title = getString(R.string.edit)

        viewModel.observePlaylist().observe(viewLifecycleOwner) { playlist ->
            binding.etPlaylistName.setText(playlist.name)
            binding.etPlaylistDescription.setText(playlist.description)
            currentPlaylist = playlist
        }

        viewModel.observeImageUrl().observe(viewLifecycleOwner) { uri ->
            if (uri != null) {
                Glide.with(binding.root)
                    .load(uri)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(LocalUtils().dpToPx(8f, binding.root))
                    )
                    .into(binding.playlistCoverStroke)

                binding.playlistCover.isVisible = false
            } else {
                binding.playlistCover.isVisible = true
                binding.playlistCover.setImageResource(R.drawable.album_cover)
            }

        }
    }

    override fun exitFromFragment() {
        findNavController().navigateUp()
    }

    override fun showSuccessToast() {
        val message =
            getString(R.string.playlist) + " ${binding.etPlaylistName.text.toString()} " + getString(R.string.edit_playlist_success)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}