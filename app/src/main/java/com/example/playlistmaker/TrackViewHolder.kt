package com.example.playlistmaker

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    private val ivTrackCover: ImageView = itemView.findViewById(R.id.ivTrackCover)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvTrackBand: TextView = itemView.findViewById(R.id.tvTrackBand)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
    @SuppressLint("RestrictedApi")
    fun bind(data: Track?) {
        val durationMillis = data!!.trackTimeMillis
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val formatted = dateFormat.format(Date(durationMillis))
        tvTrackName.text = data.trackName
        tvTrackBand.text = data.artistName
        tvTrackTime.text = formatted
        Glide.with(itemView.context)
            .load(data.artworkUrl100)
            .placeholder(R.drawable.placeholder_cover)
            .centerCrop()
            .transform(RoundedCorners(LocalUtils().dpToPx(2f, itemView.context)))
            .into(ivTrackCover)
    }
}
