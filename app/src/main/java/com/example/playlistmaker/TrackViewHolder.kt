package com.example.playlistmaker

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val artworkUrl100: TextView = itemView.findViewById(R.id.artworkUrl100)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.trackName
        trackTime.text = model.trackName
        artworkUrl100.text = model.trackName
    }

}