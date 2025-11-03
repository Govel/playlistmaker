package com.example.playlistmaker.data.repository.tracks

import com.example.playlistmaker.data.storages.network.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.mapper.TracksSearchMapper
import com.example.playlistmaker.domain.repository.tracks.TracksRepository
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>?> {
        val response = networkClient.doRequest(expression)
        return if (response is TracksSearchResponse) {
            val tracksDtoList = response.results
            val tracks = TracksSearchMapper.mapDtoListToDomain(tracksDtoList)
            Resource(expression, tracks)
        } else {
            Resource(expression, null)
        }

    }
}
