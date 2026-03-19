package com.example.Russify.data

import com.example.Russify.data.network.ApiClient
import com.example.Russify.data.network.TrackDto
import com.example.Russify.model.Album
import com.example.Russify.model.Playlist
import com.example.Russify.model.Track
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import androidx.compose.ui.graphics.Color

object MusicRepository {
    private val client = ApiClient.client


    private val coverColors = listOf(
        Color(0xFFE57373), Color(0xFFBA68C8), Color(0xFF4FC3F7),
        Color(0xFF81C784), Color(0xFFFFB74D)
    )


    suspend fun loadTracksFromServer(): List<Track> {
        return try {

            val response = client.get("/tracks")

            if (response.status == HttpStatusCode.OK) {
                val dtoList: List<TrackDto> = response.body()


                dtoList.mapIndexed { index, dto ->
                    Track(
                        id = dto.id,
                        title = dto.title,
                        artist = dto.artist,
                        durationSeconds = dto.duration,
                        audioUrl = "${ApiClient.BASE_URL}${dto.fileUrl}", // Полная ссылка на mp3
                        coverUrl = dto.coverUrl,
                        coverColor = coverColors[index % coverColors.size],
                        isFavorite = false
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    private val _playlists = mutableListOf<Playlist>()
    private val _albums = mutableListOf<Album>()

    fun getPlaylists(): List<Playlist> = _playlists
    fun getAlbums(): List<Album> = _albums

    fun createPlaylist(name: String, author: String): Playlist {
        val newId = (_playlists.maxOfOrNull { it.id } ?: 200) + 1
        val newPlaylist = Playlist(newId, name, author, false, mutableListOf())
        _playlists.add(newPlaylist)
        return newPlaylist
    }

    fun addTrackToPlaylist(playlistId: Long, track: Track) {
        val playlist = _playlists.find { it.id == playlistId }
        if (playlist != null && playlist.tracks.none { it.id == track.id }) {
            playlist.tracks.add(track)
        }
    }
}