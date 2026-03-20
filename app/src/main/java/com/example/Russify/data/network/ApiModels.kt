package com.example.Russify.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Auth Response от backend (регистрация и вход)
@Serializable
data class AuthResponse(
    val id: Long,
    val username: String,
    val email: String,
    val token: String
)

// Request для регистрации
@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

// Request для входа
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

// User DTO (если понадобится отдельно)
@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val email: String
)

// Author DTO
@Serializable
data class AuthorDto(
    val id: Long,
    val name: String,
    val photoHash: String? = null,
    val description: String? = null
)

@Serializable
data class TrackDto(
    val id: Long,
    val name: String, // Название трека (было title)
    @SerialName("audio_hash") val audioHash: String? = null,
    @SerialName("audio_url") val audioUrl: String? = null,
    @SerialName("cover_hash") val coverHash: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("album_ids") val albumIds: Set<Long> = emptySet(),
    @SerialName("author_ids") val authorIds: Set<Long> = emptySet(),
    @SerialName("genre_id") val genreId: Long? = null
)

// Playlist DTOs
@Serializable
data class PlaylistDto(
    val id: Long,
    val name: String,
    @SerialName("user_id") val userId: Long,
    @SerialName("is_system") val isSystem: Boolean,
    @SerialName("cover_hash") val coverHash: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null
)

@Serializable
data class PlaylistResponse(
    val id: Long,
    @SerialName("user_id") val userId: Long,
    val name: String,
    @SerialName("is_system") val isSystem: Boolean,
    @SerialName("cover_hash") val coverHash: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null
)

// Playlist с треками
@Serializable
data class PlaylistWithTracks(
    val id: Long,
    val name: String,
    @SerialName("user_id") val userId: Long,
    @SerialName("is_system") val isSystem: Boolean,
    @SerialName("cover_hash") val coverHash: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    val tracks: List<PlaylistTrackDto>
)

// Track внутри плейлиста
@Serializable
data class PlaylistTrackDto(
    val id: Long,
    val name: String,
    @SerialName("genre_id") val genreId: Long? = null,
    @SerialName("genre_name") val genreName: String? = null,
    @SerialName("cover_hash") val coverHash: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("audio_hash") val audioHash: String? = null,
    @SerialName("audio_url") val audioUrl: String? = null
)

// Request для добавления трека в плейлист
@Serializable
data class AddTrackToPlaylistRequest(
    @SerialName("track_id") val trackId: Long
)

// Album DTOs
@Serializable
enum class AlbumStatus {
    APPROVED,
    IN_PROGRESS,
    DENIED
}

@Serializable
data class AlbumTypeDto(
    val id: Long? = null,
    val name: String
)

@Serializable
data class AlbumDto(
    val id: Long,
    val title: String,
    val type: AlbumTypeDto? = null,
    val status: AlbumStatus,
    @SerialName("released_at") val releasedAt: String? = null,
    @SerialName("cover_hash") val coverHash: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    val tracks: Set<TrackDto>? = null,
    val authors: Set<AuthorDto>? = null
)

// Favorites DTOs
@Serializable
data class FavouriteTrackDto(
    val id: Long,
    val name: String,
    @SerialName("genre_id") val genreId: Long? = null,
    @SerialName("genre_name") val genreName: String? = null,
    @SerialName("cover_hash") val coverHash: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("audio_hash") val audioHash: String? = null,
    @SerialName("audio_url") val audioUrl: String? = null
)

// Favorites Request DTOs
@Serializable
data class AddFavouriteTrackRequest(
    @SerialName("track_id") val trackId: Long
)

@Serializable
data class AddFavouriteAlbumRequest(
    @SerialName("album_id") val albumId: Long
)

@Serializable
data class AddFavouritePlaylistRequest(
    val playlistId: Long
)

// Search DTOs
@Serializable
data class TrackSearchRequest(
    val name: String? = null,
    @SerialName("genre_ids") val genreIds: List<Long>? = null
)

@Serializable
data class TrackFlatDto(
    val id: Long,
    val name: String,
    @SerialName("genre_id") val genreId: Long? = null,
    @SerialName("genre_name") val genreName: String? = null,
    @SerialName("cover_hash") val coverHash: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("audio_hash") val audioHash: String? = null,
    @SerialName("audio_url") val audioUrl: String? = null
)
