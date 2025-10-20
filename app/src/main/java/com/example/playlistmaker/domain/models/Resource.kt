package com.example.playlistmaker.domain.models

data class Resource<T>(
    val expression: String,
    val data: T
)