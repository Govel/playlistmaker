package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.storages.network.NetworkClient
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.mapper.TracksSearchMapper
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>?> {
        return when (val response = networkClient.doRequest(expression)) {
            is TracksSearchResponse if response.resultCount > 0 -> {
                val tracksDtoList = response.results
                val tracks = TracksSearchMapper.mapDtoListToDomain(tracksDtoList)
                Resource(expression, tracks, "CONTENT")
            }

            is TracksSearchResponse if response.resultCount == 0 -> {
                Resource(expression, null, "EMPTY")
            }

            else -> {
                Resource(expression, null, "ERROR")
            }
        }

    }
}
