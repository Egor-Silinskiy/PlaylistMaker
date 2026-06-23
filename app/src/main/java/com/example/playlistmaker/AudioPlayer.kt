package com.example.playlistmaker

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class AudioPlayer : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.back).setOnClickListener {
            finish()
        }

        val track = getTrackFromIntent()

        if (track == null) {
            finish()
            return
        }

        bindTrack(track)
    }

    private fun bindTrack(track: Track) {

        val radiusInDp = 8
        val density = resources.displayMetrics.density
        val radiusInPx = (radiusInDp * density).toInt()


        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_big)
            .error(R.drawable.placeholder_big)
            .centerCrop()
            .transform(RoundedCorners(radiusInPx))
            .into(findViewById(R.id.cover))

        findViewById<TextView>(R.id.trackName).text = track.trackName
        findViewById<TextView>(R.id.artistName).text = track.artistName
        findViewById<TextView>(R.id.trackTime2).text = track.trackTime
        findViewById<TextView>(R.id.remainingTime).text = getString(R.string.remaining_time)

        bindOptionalText(
            group = findViewById(R.id.collectionNameGroup),
            textView = findViewById(R.id.collectionName2),
            value = track.collectionName
        )
        bindOptionalText(
            group = findViewById(R.id.releaseDateGroup),
            textView = findViewById(R.id.releaseDate2),
            value = track.releaseDate?.take(4)
        )

        findViewById<TextView>(R.id.primaryGenreName2).text = track.primaryGenreName.orEmpty()
        findViewById<TextView>(R.id.country2).text = track.country.orEmpty()
    }

    private fun bindOptionalText(group: Group, textView: TextView, value: String?) {
        val text = value.orEmpty()
        group.visibility = if (text.isBlank()) View.GONE else View.VISIBLE
        textView.text = text
    }

    private fun getTrackFromIntent(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_EXTRA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_EXTRA)
        }
    }

    companion object {
        const val TRACK_EXTRA = "track"
    }
}