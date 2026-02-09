package com.example.playlistmaker.media.playlists.new_playlist.data.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.example.playlistmaker.media.playlists.new_playlist.domain.api.ExternalStorageRepository
import java.io.File
import java.io.FileOutputStream


class ExternalStorageRepositoryImpl(
    private val appContext: Context
): ExternalStorageRepository {
    override fun saveImageToStorage(imageUri: Uri, imageName: String) {
        try {
            val coversDir = File(
                appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                FOLDER_COVERS
            ).apply { if (!exists()) mkdirs() }

            val file = File(coversDir, imageName)

            appContext.contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(file).use { output ->
                    BitmapFactory.decodeStream(input)?.compress(Bitmap.CompressFormat.JPEG, 30, output)
                        ?: Log.e("MyTag", "decodeStream вернул null для $imageUri")
                }
            } ?: Log.e("MyTag", "Не удалось открыть InputStream для $imageUri")
        } catch (e: Exception) {
            Log.e("MyTag", "Ошибка сохранения: ${e.message}", e)
        }
    }
    override fun loadImageFromStorage(imageName: String): Uri? {
        val file = File(
            appContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "$FOLDER_COVERS/$imageName"
        )
        return if (file.exists()) file.toUri() else null
    }

    companion object{
        const val FOLDER_COVERS = "covers"
    }
}