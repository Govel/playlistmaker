package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track

data class TracksSearchResponse(
    val resultCount: Int,
    val results: List<Track>
) : Response()