package com.example.Russify.data.repository

import android.util.Log
import com.example.Russify.data.network.AddTrackToPlaylistRequest
import com.example.Russify.data.network.ApiClient
import com.example.Russify.data.network.ApiErrorHandler
import com.example.Russify.data.network.PlaylistDto
import com.example.Russify.data.network.PlaylistResponse
import com.example.Russify.data.network.PlaylistWithTracks
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Repository для работы с плейлистами
 */
class PlaylistsRepository {
    private val client = ApiClient.client
    private val baseUrl = ApiClient.BASE_URL

    companion object {
        private const val TAG = "PlaylistsRepository"
    }

    /**
     * Получить все плейлисты
     */
    suspend fun getAllPlaylists(): Result<List<PlaylistDto>> {
        return ApiErrorHandler.safeApiCall {
            client.get("$baseUrl/playlists").body()
        }
    }

    /**
     * Получить плейлист по ID
     *
     * @param id ID плейлиста
     * @return Result с PlaylistDto или ошибкой
     */
    suspend fun getPlaylistById(id: Long): Result<PlaylistDto> {
        return ApiErrorHandler.safeApiCall {
            client.get("$baseUrl/playlists/$id").body()
        }
    }

    /**
     * Получить плейлист с треками
     *
     * @param id ID плейлиста
     * @return Result с PlaylistWithTracks или ошибкой
     */
    suspend fun getPlaylistWithTracks(id: Long): Result<PlaylistWithTracks> {
        return ApiErrorHandler.safeApiCall {
            client.get("$baseUrl/playlists/$id/tracks").body()
        }
    }

    /**
     * Создать новый плейлист
     *
     * NOTE: Backend endpoint требует multipart/form-data для загрузки coverFile.
     * Эта версия создает плейлист без обложки. Для загрузки обложки нужно
     * использовать отдельный метод с multipart request.
     *
     * @param name название плейлиста
     * @param isSystem флаг системного плейлиста (default: false)
     * @return Result с PlaylistResponse или ошибкой
     */
    suspend fun createPlaylist(
        name: String,
        isSystem: Boolean = false
    ): Result<PlaylistResponse> {
        return ApiErrorHandler.safeApiCall {
            // TODO: Реализовать загрузку coverFile через multipart/form-data
            // Для этого нужно использовать submitFormWithBinaryData или FormBuilder

            // Пока создаем плейлист без обложки через JSON
            // Backend должен принять это и установить userId из токена
            client.post("$baseUrl/playlists") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "name" to name,
                    "isSystem" to isSystem
                ))
            }.body()
        }
    }

    /**
     * Обновить плейлист
     *
     * NOTE: Backend endpoint требует multipart/form-data для загрузки coverFile.
     * Эта версия обновляет плейлист без обложки.
     *
     * @param id ID плейлиста
     * @param name новое название (optional)
     * @param isSystem новый флаг системного плейлиста (optional)
     * @return Result с PlaylistDto или ошибкой
     */
    suspend fun updatePlaylist(
        id: Long,
        name: String? = null,
        isSystem: Boolean? = null
    ): Result<PlaylistDto> {
        return ApiErrorHandler.safeApiCall {
            // TODO: Реализовать загрузку coverFile через multipart/form-data

            val updates = mutableMapOf<String, Any>()
            name?.let { updates["name"] = it }
            isSystem?.let { updates["isSystem"] = it }

            client.put("$baseUrl/playlists/$id") {
                contentType(ContentType.Application.Json)
                setBody(updates)
            }.body()
        }
    }

    /**
     * Удалить плейлист
     *
     * @param id ID плейлиста
     * @return Result<Unit> (пустой успех или ошибка)
     */
    suspend fun deletePlaylist(id: Long): Result<Unit> {
        return ApiErrorHandler.safeApiCall {
            client.delete("$baseUrl/playlists/$id")
            Unit // DELETE возвращает 204 No Content
        }
    }

    /**
     * Добавить трек в плейлист
     *
     * @param playlistId ID плейлиста
     * @param trackId ID трека
     * @return Result<Unit> (пустой успех или ошибка)
     */
    suspend fun addTrackToPlaylist(
        playlistId: Long,
        trackId: Long
    ): Result<Unit> {
        return ApiErrorHandler.safeApiCall {
            client.post("$baseUrl/playlists/$playlistId/tracks") {
                contentType(ContentType.Application.Json)
                setBody(AddTrackToPlaylistRequest(trackId))
            }
            Unit // POST возвращает 201 Created
        }
    }

    /**
     * Удалить трек из плейлиста
     *
     * @param playlistId ID плейлиста
     * @param trackId ID трека
     * @return Result<Unit> (пустой успех или ошибка)
     */
    suspend fun removeTrackFromPlaylist(
        playlistId: Long,
        trackId: Long
    ): Result<Unit> {
        return ApiErrorHandler.safeApiCall {
            client.delete("$baseUrl/playlists/$playlistId/$trackId")
            Unit // DELETE возвращает 204 No Content
        }
    }

    /**
     * Получить количество треков в плейлисте
     * (вспомогательный метод)
     *
     * @param playlistId ID плейлиста
     * @return количество треков или 0 при ошибке
     */
    suspend fun getPlaylistTrackCount(playlistId: Long): Int {
        return getPlaylistWithTracks(playlistId).getOrNull()?.tracks?.size ?: 0
    }

    /**
     * Проверить, содержит ли плейлист трек
     * (вспомогательный метод)
     *
     * @param playlistId ID плейлиста
     * @param trackId ID трека
     * @return true если трек в плейлисте, false иначе
     */
    suspend fun isTrackInPlaylist(playlistId: Long, trackId: Long): Boolean {
        val playlist = getPlaylistWithTracks(playlistId).getOrNull()
        return playlist?.tracks?.any { it.id == trackId } ?: false
    }
}
