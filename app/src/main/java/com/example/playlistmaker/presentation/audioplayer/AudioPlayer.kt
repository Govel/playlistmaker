package com.example.playlistmaker.presentation.audioplayer

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.LocalUtils
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.TAG_CURRENT_TRACK
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar

class AudioPlayer : AppCompatActivity() {
    private val mediaPlayerInteractor = Creator.provideMediaPlayerInteractor()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var materialToolbar: MaterialToolbar
    private lateinit var ivArtwork: ImageView
    private lateinit var main: ConstraintLayout
    private lateinit var tvTrackName: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var tvTrackTimeMillis: TextView
    private lateinit var tvCollectionTrackName: TextView
    private lateinit var tvReleaseTrackDate: TextView
    private lateinit var tvPrimaryGenreTrackName: TextView
    private lateinit var tvTrackCountry: TextView
    private lateinit var tvTrackTime: TextView
    private lateinit var groupAlbum: Group
    private lateinit var groupYear: Group
    private lateinit var btAddToPlaylist: ImageView
    private lateinit var btPlay: ImageView
    private lateinit var btAddFavorite: ImageView
    private lateinit var currentTrack: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.start)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        materialToolbar = findViewById(R.id.mtb_arrowback)
        ivArtwork = findViewById(R.id.iv_artwork)
        main = findViewById(R.id.main)
        tvTrackName = findViewById(R.id.tv_track_name)
        tvArtistName = findViewById(R.id.tv_artist_name)
        tvTrackTimeMillis = findViewById(R.id.tv_track_time_millis)
        tvCollectionTrackName = findViewById(R.id.tv_collection_track_name)
        tvReleaseTrackDate = findViewById(R.id.tv_release_track_date)
        tvPrimaryGenreTrackName = findViewById(R.id.tv_primary_genre_track_name)
        tvTrackCountry = findViewById(R.id.tv_track_country)
        tvTrackTime = findViewById(R.id.tv_track_time)
        groupAlbum = findViewById(R.id.group_album)
        groupYear = findViewById(R.id.group_year)
        btAddToPlaylist = findViewById(R.id.bt_add_to_playlist)
        btPlay = findViewById(R.id.bt_play)
        btAddFavorite = findViewById(R.id.bt_add_favorite)

        materialToolbar.setNavigationOnClickListener { finish() }
        currentTrack = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TAG_CURRENT_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TAG_CURRENT_TRACK)
        })!!

        Glide.with(main.context)
            .load(
                currentTrack.getCoverArtwork()
            )
            .placeholder(R.drawable.placeholder_cover)
            .fitCenter()
            .transform(
                RoundedCorners(
                    LocalUtils().dpToPx(8.0f, main)
                )
            )
            .into(ivArtwork)

        tvTrackName.text = currentTrack.trackName
        tvArtistName.text = currentTrack.artistName
        tvTrackTimeMillis.text = LocalUtils().dateFormat(currentTrack.trackTimeMillis)
        tvTrackTime.text =
            LocalUtils().dateFormat(mediaPlayerInteractor.getCurrentPosition().toLong())
        if (currentTrack.collectionName.isNullOrEmpty()) {
            groupAlbum.isVisible = false
        } else {
            groupAlbum.isVisible = true
            tvCollectionTrackName.text = currentTrack.collectionName
        }
        if (currentTrack.releaseDate.isNullOrEmpty()) {
            groupYear.isVisible = false
        } else {
            groupYear.isVisible = true
            tvReleaseTrackDate.text = currentTrack.getYearTrack()
        }
        tvPrimaryGenreTrackName.text = currentTrack.primaryGenreName ?: ""
        tvTrackCountry.text = currentTrack.country ?: ""

        mediaPlayerInteractor.preparePlayer(currentTrack.previewUrl)
        btPlay.setOnClickListener {
            mediaPlayerInteractor.playbackControl()
            setImageButtonPlay()
            setTimer()
        }

    }

    override fun onPause() {
        super.onPause()
        mediaPlayerInteractor.pausePlayer()
    }

    @SuppressLint("ImplicitSamInstance")
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerInteractor.releasePlayer()
    }

    private fun setTimer() {
        handler.postDelayed(
            object : Runnable {
                @SuppressLint("SimpleDateFormat")
                override fun run() {
                    if (mediaPlayerInteractor.isStatePlayerPlaying()) {
                        tvTrackTime.text =
                            LocalUtils().dateFormat(
                                mediaPlayerInteractor.getCurrentPosition().toLong()
                            )
                        handler.postDelayed(this, DELAY_MILLIS)
                    } else {
                        handler.removeCallbacks(this)
                        if (mediaPlayerInteractor.isStatePlayerPrepared()) {
                            tvTrackTime.text = LocalUtils().dateFormat(0L)
                            setImageButtonPlay()
                        }
                    }
                }
            }, DELAY_MILLIS
        )
    }

    private fun setImageButtonPlay() {
        if (mediaPlayerInteractor.isStatePlayerPlaying()) {
            btPlay.setImageResource(R.drawable.pause)
        } else {
            btPlay.setImageResource(R.drawable.play)
        }
    }

    companion object {
        private const val DELAY_MILLIS = 400L
    }
}
