package com.example.playlistmaker

data class Track(
    val trackId: Long, // Id композиции в базе iTunes //
    val trackName: String, // Название композиции //
    val artistName: String, // Имя исполнителя //
    val trackTime: String, // Продолжительность трека //
    val artworkUrl: String // Ссылка на изображение обложки //
)
