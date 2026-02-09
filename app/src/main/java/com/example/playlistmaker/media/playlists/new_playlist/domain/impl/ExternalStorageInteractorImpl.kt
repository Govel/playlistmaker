package com.example.playlistmaker.media.playlists.new_playlist.domain.impl

import android.net.Uri
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalStorageInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalStorageRepository

class ExternalStorageInteractorImpl(
    val externalStorageRepository: ExternalStorageRepository
) : ExternalStorageInteractor{
    override fun saveImageToStorage(imageUri: Uri, imageName: String) {
        externalStorageRepository.saveImageToStorage(imageUri, imageName)
    }

    override fun loadImageFromStorage(imageName: String): Uri? =
        externalStorageRepository.loadImageFromStorage(imageName)
}