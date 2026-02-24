package com.example.playlistmaker.media.playlists.playlist.ui.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface TracksState {
    object Empty: TracksState
    data class Content(val tracks: List<Track>): TracksState
}