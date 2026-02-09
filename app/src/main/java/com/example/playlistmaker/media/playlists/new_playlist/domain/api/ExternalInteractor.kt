package com.example.playlistmaker.media.playlists.new_playlist.domain.api

import android.net.Uri

fun interface ExternalInteractor {
    fun getUriByCoverName(coverName: String): Uri?
}