package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val track: MutableList<Track>,
    private val onTrackClick: ((Track) -> Unit)? = null
) : RecyclerView.Adapter<TrackViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val item = track[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onTrackClick?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return track.size
    }

    fun setTracks(tracks: List<Track>) {
        track.clear()
        track.addAll(tracks)
        notifyDataSetChanged()
    }

}