package com.example.playlistmaker.search.data.storages.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.dto.NetworkResponse

class RetrofitNetworkClient(private val context: Context) : NetworkClient {

    override fun doRequest(dto: String): NetworkResponse {
        if (!isConnected()) {
            return NetworkResponse().apply {resultCode = -1}
        }
        return try {
            val resp = RetrofitClient.api.searchTracks(dto).execute()
            val body = resp.body() ?: NetworkResponse()
            body.apply { resultCode = resp.code() }
        } catch (ex: Exception) {
            NetworkResponse().apply { resultCode = 400 }
        }
    }
    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}