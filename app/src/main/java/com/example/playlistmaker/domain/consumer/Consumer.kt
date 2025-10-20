package com.example.playlistmaker.domain.consumer

import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track

interface Consumer {
    fun consume(tracks: Resource<List<Track>?>)
}