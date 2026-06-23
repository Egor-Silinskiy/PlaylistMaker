package com.example.playlistmaker

data class Track(
    val trackId: Long, // Id композиции в базе iTunes //
    val trackName: String, // Название композиции //
    val artistName: String, // Имя исполнителя //
    val trackTime: String, // Продолжительность трека //
    val artworkUrl100: String, // Ссылка на изображение обложки //
    val collectionName: String?, // Альбом //
    val releaseDate: String?, // Год //
    val primaryGenreName: String?, // Жанр //
    val country: String? // Страна //
) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
