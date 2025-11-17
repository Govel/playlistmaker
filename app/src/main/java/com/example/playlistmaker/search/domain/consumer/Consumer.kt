package com.example.playlistmaker.search.domain.consumer

import com.example.playlistmaker.search.domain.models.Resource
import com.example.playlistmaker.search.domain.models.Track

interface Consumer {
    fun consume(tracks: Resource<List<Track>?>)
}