package com.example.playlistmaker.search.domain.models

data class Resource<T>(
    val expression: String,
    val data: T
)
