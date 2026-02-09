package com.example.playlistmaker.util

class FormatTracksCount {
    fun format(tracksCount: Int?): String {
        val lastDigit = tracksCount!! % 10
        val lastTwoDigits: Int = tracksCount % 100
        return when {
            lastTwoDigits in 11..14 -> "$tracksCount треков"
            lastDigit == 1 -> "$tracksCount трек"
            lastDigit in 2..4 -> "$tracksCount трека"
            else -> "$tracksCount треков"
        }
    }
}