package com.example.playlistmaker.db.data.converter

import com.example.playlistmaker.db.data.entity.PlaylistEntity
import com.example.playlistmaker.media.playlists.new_playlist.domain.models.Playlist

class PlaylistDbConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.imgPath,
            playlist.tracksList,
            playlist.totalTracks
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.imgPath,
            playlist.tracksList,
            playlist.totalTracks
        )
    }
}