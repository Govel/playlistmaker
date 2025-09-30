package com.example.playlistmaker.ui.tracks

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.LocalUtils
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    private val ivTrackCover: ImageView = itemView.findViewById(R.id.ivTrackCover)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvTrackBand: TextView = itemView.findViewById(R.id.tvTrackBand)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
    @SuppressLint("RestrictedApi")
    fun bind(data: Track?) {
        tvTrackName.text = data?.trackName
        tvTrackBand.text = data?.artistName
        tvTrackTime.text = LocalUtils().dateFormat(data!!.trackTimeMillis)
        Glide.with(itemView.context)
            .load(data.artworkUrl100)
            .placeholder(R.drawable.placeholder_cover)
            .centerCrop()
            .transform(RoundedCorners(LocalUtils().dpToPx(2f, itemView)))
            .into(ivTrackCover)
    }
}
