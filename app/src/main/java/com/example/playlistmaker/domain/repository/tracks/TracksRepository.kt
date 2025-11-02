package com.example.playlistmaker.domain.repository.tracks

import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String) : Resource<List<Track>?>
}
