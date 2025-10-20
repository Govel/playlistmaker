package com.example.playlistmaker.data.storages.local

interface SharedPrefsClient<T> {
    fun save(data: T)

    fun load(data: T): T

    fun clear(data: T)
}