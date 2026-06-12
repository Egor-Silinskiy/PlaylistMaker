package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class SearchHistory(

    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
    ) {


    fun getHistory(): List<Track> {
        val historyJson = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyList()
        return runCatching {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson<List<Track>>(historyJson, type)
        }.getOrDefault(emptyList())
    }

    fun addTrack(track: Track) {
        val history = getHistory()
            .filter { it.trackId != track.trackId }
            .toMutableList()

        history.add(0, track)

        sharedPreferences.edit {
            putString(HISTORY_KEY, gson.toJson(history.take(MAX_HISTORY_SIZE)))
        }
    }

    fun clearHistory() {
        sharedPreferences.edit {
            remove(HISTORY_KEY)
        }
    }

    companion object {
        private const val HISTORY_KEY = "SEARCH_HISTORY"
        private const val MAX_HISTORY_SIZE = 10
    }
}