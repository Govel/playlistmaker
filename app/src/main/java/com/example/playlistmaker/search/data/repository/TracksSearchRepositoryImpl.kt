package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.mapper.TracksSearchMapper
import com.example.playlistmaker.search.data.storages.network.NetworkClient
import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksSearchRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>?>> = flow {
        val response = networkClient.doRequest(expression)
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету!"))
            }

            200 -> {
                val tracksResponse = response as TracksSearchResponse
                if (tracksResponse.resultCount > 0) {
                    emit(Resource.Success(TracksSearchMapper.mapDtoListToDomain(response.results)))
                } else {
                    emit(Resource.Error("EMPTY"))
                }
            }

            else -> {
                emit(Resource.Error("ERROR"))
            }
        }
    }
}
