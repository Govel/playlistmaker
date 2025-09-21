package com.example.playlistmaker

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val TAG_CURRENT_TRACK = "current_track"
@Parcelize
data class Track (
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?
) : Parcelable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    fun getYearTrack() = releaseDate?.split('-')[0]
}
