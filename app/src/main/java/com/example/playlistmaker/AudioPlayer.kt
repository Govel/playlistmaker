package com.example.playlistmaker

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.google.android.material.appbar.MaterialToolbar

class AudioPlayer : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private var mediaPlayer = MediaPlayer()
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
        tvTrackTime.text = LocalUtils().dateFormat(mediaPlayer.currentPosition.toLong())
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

        preparePlayer()
        btPlay.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    @SuppressLint("ImplicitSamInstance")
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks { runnable() }
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    @SuppressLint("ImplicitSamInstance")
    private fun preparePlayer() {
        mediaPlayer.setDataSource(currentTrack.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            btPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            btPlay.setImageResource(R.drawable.play)
            playerState = STATE_PREPARED
            handler.removeCallbacks { runnable() }
            tvTrackTime.text = "00:00"
        }
    }

    @SuppressLint("ImplicitSamInstance")
    private fun startPlayer() {
        mediaPlayer.start()
        btPlay.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        handler.removeCallbacks { runnable() }
        handler.post(runnable())

    }

    @SuppressLint("ImplicitSamInstance")
    private fun pausePlayer() {
        mediaPlayer.pause()
        btPlay.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
        handler.removeCallbacks { runnable() }
    }
    private fun runnable() : Runnable {
        return object : Runnable {
            @SuppressLint("SimpleDateFormat")
            override fun run() {
                if (playerState == STATE_PLAYING) {
                    tvTrackTime.text = LocalUtils().dateFormat(mediaPlayer.currentPosition.toLong() + 1)
                    Log.d("MyTag", tvTrackTime.text.toString())
                    handler.postDelayed(this, 400L)
                }
            }
        }
    }

}