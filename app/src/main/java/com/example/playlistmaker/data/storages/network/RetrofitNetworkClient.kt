package com.example.playlistmaker.data.storages.network

import com.example.playlistmaker.data.storages.network.NetworkClient
import com.example.playlistmaker.data.dto.NetworkResponse

class RetrofitNetworkClient : NetworkClient {

    override fun doRequest(dto: String): NetworkResponse {
        return try {
            val resp = RetrofitClient.api.searchTracks(dto).execute()
            val body = resp.body() ?: NetworkResponse()
            body.apply { resultCode = resp.code() }
        } catch (ex: Exception) {
            NetworkResponse().apply { resultCode = 400 }
        }
    }
}