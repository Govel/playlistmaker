package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.storages.network.NetworkClient
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.mapper.TracksSearchMapper
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track

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
