package com.example.playlistmaker.search.domain.repository

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String) : Flow<Resource<List<Track>?>>
}
