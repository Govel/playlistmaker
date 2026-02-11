package com.example.playlistmaker.media.playlists.new_playlist.ui

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalStorageInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


open class NewPlaylistViewModel(
    protected val playlistsInteractor: PlaylistsInteractor,
    protected val externalStorageInteractor: ExternalStorageInteractor
): ViewModel() {

    protected val imageUrlLiveData = MutableLiveData<Uri?>(null)
    open fun observeImageUrl(): LiveData<Uri?> = imageUrlLiveData

    protected val saveResult = MutableLiveData<Boolean>()
    open fun observeSaveResult(): LiveData<Boolean> = saveResult

    open fun addToDb(playlistName: String, playlistDescription: String) {
        val coverFileName = "${playlistName}_${System.currentTimeMillis()}.jpg"
        viewModelScope.launch {
            try {
                imageUrlLiveData.value?.let { uri ->
                    withContext(Dispatchers.IO) {
                        externalStorageInteractor.saveImageToStorage(uri, coverFileName)
                    }
                }
                playlistsInteractor.addPlaylist(Playlist(id = 0, name = playlistName, description = playlistDescription, imgPath = coverFileName, tracksList = "", totalTracks = 0))
                saveResult.value = true
            } catch (e: Exception) {
                saveResult.value = false
                Log.e("NewPlaylistViewModel", "Ошибка создания плейлиста", e)
            }
        }
    }

    fun setImageUri(uri: Uri?) {
        imageUrlLiveData.value = uri
    }

    fun isImageCoverSet() = imageUrlLiveData.value != null
}