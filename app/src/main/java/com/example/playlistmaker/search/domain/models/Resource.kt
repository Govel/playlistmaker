package com.example.playlistmaker.search.domain.models

//data class Resource<T>(
//    val expression: String,
//    val data: T,
//    val message: String?
//)
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
}