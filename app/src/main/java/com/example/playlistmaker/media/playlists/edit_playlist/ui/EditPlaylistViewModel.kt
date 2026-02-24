package com.example.playlistmaker.media.playlists.edit_playlist.ui


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.db.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalStorageInteractor
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.media.playlists.new_playlist.ui.NewPlaylistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class EditPlaylistViewModel(
    private val playlistId: Int,
    playlistsInteractor: PlaylistsInteractor,
    externalStorageInteractor: ExternalStorageInteractor
) : NewPlaylistViewModel(playlistsInteractor,externalStorageInteractor) {

    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylist(): LiveData<Playlist> = playlistLiveData

    private var currentPlaylist: Playlist? = null

    fun init(playlistId: Int) {
        viewModelScope.launch {
            val playlist = withContext(Dispatchers.IO) {
                playlistsInteractor.getPlaylistById(playlistId)
            }
            currentPlaylist = playlist
            playlistLiveData.value = playlist

            playlist.imgPath.let { imageName ->
                val uri = externalStorageInteractor.loadImageFromStorage(imageName)
                imageUrlLiveData.value = uri
            }
        }
    }

    override fun addToDb(playlistName: String, playlistDescription: String) {
        val oldPlaylist  = currentPlaylist ?: return

        viewModelScope.launch {
            try {
                val newImageName = if (imageUrlLiveData.value != null &&
                    imageUrlLiveData.value != externalStorageInteractor.loadImageFromStorage(oldPlaylist.imgPath)
                ) {
                    // выбрали новую картинку
                    val fileName = "${playlistName}_${System.currentTimeMillis()}.jpg"

                    withContext(Dispatchers.IO) {
                        externalStorageInteractor.saveImageToStorage(
                            imageUrlLiveData.value!!,
                            fileName
                        )
                    }
                    fileName
                } else {
                    oldPlaylist.imgPath
                }

                val updatedPlaylist = oldPlaylist.copy(
                    name = playlistName,
                    description = playlistDescription,
                    imgPath = newImageName
                )

                withContext(Dispatchers.IO) {
                    playlistsInteractor.updatePlaylist(updatedPlaylist)
                }

                saveResult.value = true
            } catch (e: Exception) {
                saveResult.value = false
                Log.e("EditPlaylistViewModel", "Ошибка обновления плейлиста", e)
            }
        }
    }
}
