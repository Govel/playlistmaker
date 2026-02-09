package com.example.playlistmaker.media.playlists.new_playlist.domain.api

import android.net.Uri

interface ExternalStorageRepository {
    fun saveImageToStorage(imageUri: Uri, imageName: String)

    fun loadImageFromStorage(imageName: String): Uri?
}