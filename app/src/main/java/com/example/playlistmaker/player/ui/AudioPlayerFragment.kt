package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.example.playlistmaker.player.ui.models.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.LocalUtils
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf

class AudioPlayerFragment : Fragment() {
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private val args: AudioPlayerFragmentArgs by navArgs()
    private val track: Track get() = args.currentTrack
    private lateinit var currentTrack: Track

    private lateinit var viewModel: AudioPlayerViewModel

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
        binding.btPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun enableButton(isEnabled: Boolean) {
        binding.btPlay.isEnabled = isEnabled
    }

    private fun setImageButtonPlay(buttonText: String) {
        binding.btPlay.setImageResource(if (buttonText == "PAUSE") R.drawable.pause else R.drawable.play)
    }
    fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> showProgressBar()
            else -> showContent()
        }
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