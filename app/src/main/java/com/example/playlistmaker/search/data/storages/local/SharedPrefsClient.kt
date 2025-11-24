package com.example.playlistmaker.search.data.storages.local

interface SharedPrefsClient<T> {
    fun save(data: T)
    fun load(data: T): T
    fun clear()
}