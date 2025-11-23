package com.example.playlistmaker.search.data.storages.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val I_TUNES_BASE_URL = "https://itunes.apple.com"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(I_TUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val api: ITunesApi by lazy {
        retrofit.create(ITunesApi::class.java)
    }
}