package com.example.Russify.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto
)

@Serializable
data class UserDto(
    @SerialName("user_id") val userId: Long,
    val username: String,
    val email: String
)

@Serializable
data class TrackDto(
    val id: Long,
    val title: String,
    val artist: String = "Неизвестен", // Если с бэка не приходит, ставим дефолт
    val duration: Int = 180, // Если бэк не отдает длину в секундах
    @SerialName("file_url") val fileUrl: String, // ССЫЛКА НА МУЗЫКУ
    @SerialName("cover_url") val coverUrl: String? = null
)