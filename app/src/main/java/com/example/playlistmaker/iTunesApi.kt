package com.example.playlistmaker

import android.telecom.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface iTunesApi {
    @GET("/search?entity=song")
    fun search(@Query("term") text: String): Call<ITunesSearchResponse>
}