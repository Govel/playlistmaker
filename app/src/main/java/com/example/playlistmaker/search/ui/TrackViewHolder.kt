package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.LocalUtils
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.RvTracksBinding
import com.example.playlistmaker.search.domain.models.Track

class TrackViewHolder(private val binding: RvTracksBinding) : RecyclerView.ViewHolder(binding.root) {


    @SuppressLint("RestrictedApi")
    fun bind(data: Track?) {
        binding.tvTrackName.text = data?.trackName
        binding.tvTrackBand.text = data?.artistName
        binding.tvTrackTime.text = LocalUtils().dateFormat(data!!.trackTimeMillis)
        Glide.with(binding.root)
            .load(data.artworkUrl100)
            .placeholder(R.drawable.placeholder_cover)
            .centerCrop()
            .transform(RoundedCorners(LocalUtils().dpToPx(2f, itemView)))
            .into(binding.ivTrackCover)
    }
    companion object {
        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RvTracksBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }
}
