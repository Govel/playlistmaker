package com.example.playlistmaker.search.data.storages.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.playlistmaker.search.data.dto.NetworkResponse
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class RetrofitNetworkClient(
    private val iTunesApi: ITunesApi,
    private val context: Context
) : NetworkClient {

    override suspend fun doRequest(dto: String): NetworkResponse {
        if (!isConnected()) {
            return NetworkResponse().apply {resultCode = -1}
        }
        return withContext(Dispatchers.IO) {
            try {
                val resp = iTunesApi.searchTracks(dto)
                resp.apply { resultCode = 200 }
            } catch (ex: Exception) {
                NetworkResponse().apply { resultCode = 400 }
            }
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