package com.example.playlistmaker.media.playlists.playlist.data

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.R
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist
import com.example.playlistmaker.media.playlists.playlist.domain.api.PlaylistRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.LocalUtils

class PlaylistRepositoryImpl(private val context: Context): PlaylistRepository {
    override fun shareLink(playlist: Playlist?, tracks: List<Track>) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, getText(playlist, tracks))
        Intent.createChooser(shareIntent, null)
        context.startActivity(Intent.createChooser(shareIntent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun getText(playlist: Playlist?,tracks: List<Track>) : String {
        var text = "${playlist?.name}\n${playlist?.description}\n" + context.resources.getQuantityString(R.plurals.tracks, tracks.size, tracks.size)
        for (i in 0 until tracks.size) {
            val track = tracks[i]
            text += "\n${i + 1}. ${track.artistName} - ${track.trackName} (${LocalUtils().dateFormat(track.trackTimeMillis)})"
        }
        return text
    }
}