package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    private val rootLayout: LinearLayout = itemView.findViewById(R.id.rootLayout)
    private val ivTrackCover: ImageView = itemView.findViewById(R.id.ivTrackCover)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvTrackBand: TextView = itemView.findViewById(R.id.tvTrackBand)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
    @SuppressLint("RestrictedApi")
    fun bind(data: Track) {
        tvTrackName.text = data.trackName
        tvTrackBand.text = data.artistName
        tvTrackTime.text = data.trackTime
//        val imageCoverRadius = dpToPx(2f, itemView.context)
        Glide.with(itemView.context)
            .load(data.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(ivTrackCover)
    }
    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}