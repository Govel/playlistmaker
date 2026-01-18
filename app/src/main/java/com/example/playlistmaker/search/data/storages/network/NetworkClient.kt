package com.example.playlistmaker.search.data.storages.network

import com.example.playlistmaker.search.data.dto.NetworkResponse

interface NetworkClient {
    suspend fun doRequest(dto: String) : NetworkResponse
}