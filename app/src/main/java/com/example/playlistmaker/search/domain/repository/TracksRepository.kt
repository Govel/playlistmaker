package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String) : Resource<List<Track>?>
}
