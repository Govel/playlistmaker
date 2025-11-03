package com.example.playlistmaker.data.storages.network

import com.example.playlistmaker.data.dto.NetworkResponse

interface NetworkClient {
    fun doRequest(dto: String) : NetworkResponse
}