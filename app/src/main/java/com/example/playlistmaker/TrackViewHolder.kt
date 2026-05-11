package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val artwork: ImageView = itemView.findViewById(R.id.artworkUrl)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(artwork)

        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
    }

}