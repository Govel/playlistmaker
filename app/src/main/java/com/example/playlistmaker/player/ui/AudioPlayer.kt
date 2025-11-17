package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.LocalUtils
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.player.ui.model.PlayerState
import com.example.playlistmaker.search.domain.models.TAG_CURRENT_TRACK
import com.example.playlistmaker.search.domain.models.Track

class AudioPlayer : AppCompatActivity() {
    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()

    private lateinit var currentTrack: Track

    private lateinit var binding: ActivityAudioplayerBinding
    private lateinit var viewModel: AudioPlayerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.start)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d("MyTag", "")


        binding.mtbArrowback.setNavigationOnClickListener { finish() }
        currentTrack = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TAG_CURRENT_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TAG_CURRENT_TRACK)
        })!!
        viewModel =
            ViewModelProvider(this, AudioPlayerViewModel.getFactory(currentTrack.previewUrl)).get(
                AudioPlayerViewModel::class.java
            )
        Glide.with(binding.main.context)
            .load(
                currentTrack.getCoverArtwork()
            )
            .placeholder(R.drawable.placeholder_cover)
            .fitCenter()
            .transform(
                RoundedCorners(
                    LocalUtils().dpToPx(8.0f, binding.main)
                )
            )
            .into(binding.ivArtwork)

        binding.tvTrackName.text = currentTrack.trackName
        binding.tvArtistName.text = currentTrack.artistName
        binding.tvTrackTimeMillis.text = LocalUtils().dateFormat(currentTrack.trackTimeMillis)
        binding.tvTrackTime.text = "00:00"
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
        viewModel.observePlayerStatus().observe(this) {
            Log.d("MyTag", "playerState: ${it.playerState}")
            setImageButtonPlay(it.playerState == PlayerState.STATE_PLAYING)
            enableButton(it.playerState != PlayerState.STATE_DEFAULT)
            binding.tvTrackTime.text = it.timer

        }
        binding.btPlay.setOnClickListener {
            Log.d("MyTag", "clicked")
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

    private fun setImageButtonPlay(isPlaying: Boolean) {
        binding.btPlay.setImageResource(if (isPlaying) R.drawable.pause else R.drawable.play)
    }
}
