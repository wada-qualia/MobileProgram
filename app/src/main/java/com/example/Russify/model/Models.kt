package com.example.Russify.model

import androidx.compose.ui.graphics.Color

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val durationSeconds: Int,
    val coverUrl: String? = null,
    val genreId: Long = 0,
    val coverColor: Color = Color(0xFFE57373),
    var isFavorite: Boolean = false,
    val audioUrl: String = ""
) {
    val durationFormatted: String
        get() {
            val m = durationSeconds / 60
            val s = durationSeconds % 60
            return "%02d:%02d".format(m, s)
        }
}

data class Playlist(
    val id: Long,
    val title: String,
    val authorName: String,
    val isSystem: Boolean = false,
    val tracks: MutableList<Track> = mutableListOf()
)

data class Album(
    val id: Long,
    val title: String,
    val authorName: String,
    val tracks: List<Track>
)

sealed class PlayingContext {
    object AllTracks : PlayingContext()
    data class PlaylistContext(val name: String) : PlayingContext()
    data class AlbumContext(val name: String) : PlayingContext()
}