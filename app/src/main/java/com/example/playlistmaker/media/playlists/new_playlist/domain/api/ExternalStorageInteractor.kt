package com.example.playlistmaker.media.playlists.new_playlist.domain.api

import android.net.Uri

interface ExternalStorageInteractor {
    fun saveImageToStorage(imageUri: Uri, imageName: String)

    fun loadImageFromStorage(imageName: String): Uri?
}