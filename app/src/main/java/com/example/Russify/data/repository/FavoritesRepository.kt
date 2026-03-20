package com.example.Russify.data.repository

import android.util.Log
import com.example.Russify.data.network.AddFavouriteAlbumRequest
import com.example.Russify.data.network.AddFavouritePlaylistRequest
import com.example.Russify.data.network.AddFavouriteTrackRequest
import com.example.Russify.data.network.AlbumDto
import com.example.Russify.data.network.ApiClient
import com.example.Russify.data.network.ApiErrorHandler
import com.example.Russify.data.network.FavouriteTrackDto
import com.example.Russify.data.network.PlaylistDto
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Repository для работы с избранным (треки, альбомы, плейлисты)
 */
class FavoritesRepository {
    private val client = ApiClient.client
    private val baseUrl = ApiClient.BASE_URL

    companion object {
        private const val TAG = "FavoritesRepository"
    }

    // ============================================================
    // TRACKS - Треки в избранном
    // ============================================================

    /**
     * Получить все избранные треки
     *
     * @return Result с List<FavouriteTrackDto> или ошибкой
     */
    suspend fun getFavouriteTracks(): Result<List<FavouriteTrackDto>> {
        return ApiErrorHandler.safeApiCall {
            client.get("$baseUrl/favorites/tracks").body()
        }
    }

    /**
     * Добавить трек в избранное
     *
     * @param trackId ID трека
     * @return Result<Unit> (пустой успех или ошибка)
     */
    suspend fun addFavouriteTrack(trackId: Long): Result<Unit> {
        return ApiErrorHandler.safeApiCall {
            client.post("$baseUrl/favorites/tracks") {
                contentType(ContentType.Application.Json)
                setBody(AddFavouriteTrackRequest(trackId))
            }
            Unit // POST возвращает 201 Created
        }
    }

    /**
     * Удалить трек из избранного
     *
     * @param trackId ID трека
     * @return Result<Unit> (пустой успех или ошибка)
     */
    suspend fun deleteFavouriteTrack(trackId: Long): Result<Unit> {
        return ApiErrorHandler.safeApiCall {
            client.delete("$baseUrl/favorites/tracks/$trackId")
            Unit // DELETE возвращает 204 No Content
        }
    }

    /**
     * Проверить, находится ли трек в избранном
     * (вспомогательный метод)
     *
     * @param trackId ID трека
     * @return true если трек в избранном, false иначе
     */
    suspend fun isTrackFavourite(trackId: Long): Boolean {
        val favourites = getFavouriteTracks().getOrNull()
        return favourites?.any { it.id == trackId } ?: false
    }

    // ============================================================
    // ALBUMS - Альбомы в избранном
    // ============================================================

    /**
     * Получить все избранные альбомы
     *
     * @return Result с List<AlbumDto> или ошибкой
     */
    suspend fun getFavouriteAlbums(): Result<List<AlbumDto>> {
        return ApiErrorHandler.safeApiCall {
            client.get("$baseUrl/favorites/albums").body()
        }
    }

    /**
     * Добавить альбом в избранное
     *
     * @param albumId ID альбома
     * @return Result<Unit> (пустой успех или ошибка)
     */
    suspend fun addFavouriteAlbum(albumId: Long): Result<Unit> {
        return ApiErrorHandler.safeApiCall {
            client.post("$baseUrl/favorites/albums") {
                contentType(ContentType.Application.Json)
                setBody(AddFavouriteAlbumRequest(albumId))
            }
            Unit // POST возвращает 201 Created
        }
    }

    /**
     * Удалить альбом из избранного
     *
     * @param albumId ID альбома
     * @return Result<Unit> (пустой успех или ошибка)
     */
    suspend fun deleteFavouriteAlbum(albumId: Long): Result<Unit> {
        return ApiErrorHandler.safeApiCall {
            client.delete("$baseUrl/favorites/albums/$albumId")
            Unit // DELETE возвращает 204 No Content
        }
    }

    /**
     * Проверить, находится ли альбом в избранном
     * (вспомогательный метод)
     *
     * @param albumId ID альбома
     * @return true если альбом в избранном, false иначе
     */
    suspend fun isAlbumFavourite(albumId: Long): Boolean {
        val favourites = getFavouriteAlbums().getOrNull()
        return favourites?.any { it.id == albumId } ?: false
    }

    // ============================================================
    // PLAYLISTS - Плейлисты в избранном
    // ============================================================

    /**
     * Получить все избранные плейлисты
     *
     * @return Result с List<PlaylistDto> или ошибкой
     */
    suspend fun getFavouritePlaylists(): Result<List<PlaylistDto>> {
        return ApiErrorHandler.safeApiCall {
            client.get("$baseUrl/favorites/playlists").body()
        }
    }

    /**
     * Добавить плейлист в избранное
     *
     * @param playlistId ID плейлиста
     * @return Result<Unit> (пустой успех или ошибка)
     */
    suspend fun addFavouritePlaylist(playlistId: Long): Result<Unit> {
        return ApiErrorHandler.safeApiCall {
            client.post("$baseUrl/favorites/playlists") {
                contentType(ContentType.Application.Json)
                setBody(AddFavouritePlaylistRequest(playlistId))
            }
            Unit // POST возвращает 201 Created
        }
    }

    /**
     * Удалить плейлист из избранного
     *
     * @param playlistId ID плейлиста
     * @return Result<Unit> (пустой успех или ошибка)
     */
    suspend fun removeFavouritePlaylist(playlistId: Long): Result<Unit> {
        return ApiErrorHandler.safeApiCall {
            client.delete("$baseUrl/favorites/playlists/$playlistId")
            Unit // DELETE возвращает 204 No Content
        }
    }

    /**
     * Проверить, находится ли плейлист в избранном
     * (вспомогательный метод)
     *
     * @param playlistId ID плейлиста
     * @return true если плейлист в избранном, false иначе
     */
    suspend fun isPlaylistFavourite(playlistId: Long): Boolean {
        val favourites = getFavouritePlaylists().getOrNull()
        return favourites?.any { it.id == playlistId } ?: false
    }

    // ============================================================
    // BATCH OPERATIONS - Пакетные операции
    // ============================================================

    /**
     * Получить все избранное (треки, альбомы, плейлисты)
     * (вспомогательный метод для удобства)
     *
     * @return Triple с (треки, альбомы, плейлисты) или пустыми списками при ошибке
     */
    suspend fun getAllFavourites(): Triple<List<FavouriteTrackDto>, List<AlbumDto>, List<PlaylistDto>> {
        val tracks = getFavouriteTracks().getOrNull() ?: emptyList()
        val albums = getFavouriteAlbums().getOrNull() ?: emptyList()
        val playlists = getFavouritePlaylists().getOrNull() ?: emptyList()

        return Triple(tracks, albums, playlists)
    }

    /**
     * Переключить статус избранного для трека (toggle)
     *
     * @param trackId ID трека
     * @return Result<Boolean> где true = добавлено, false = удалено
     */
    suspend fun toggleFavouriteTrack(trackId: Long): Result<Boolean> {
        return try {
            val isFavourite = isTrackFavourite(trackId)
            if (isFavourite) {
                deleteFavouriteTrack(trackId).map { false }
            } else {
                addFavouriteTrack(trackId).map { true }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling favourite track", e)
            Result.failure(e)
        }
    }

    /**
     * Переключить статус избранного для альбома (toggle)
     *
     * @param albumId ID альбома
     * @return Result<Boolean> где true = добавлено, false = удалено
     */
    suspend fun toggleFavouriteAlbum(albumId: Long): Result<Boolean> {
        return try {
            val isFavourite = isAlbumFavourite(albumId)
            if (isFavourite) {
                deleteFavouriteAlbum(albumId).map { false }
            } else {
                addFavouriteAlbum(albumId).map { true }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling favourite album", e)
            Result.failure(e)
        }
    }

    /**
     * Переключить статус избранного для плейлиста (toggle)
     *
     * @param playlistId ID плейлиста
     * @return Result<Boolean> где true = добавлено, false = удалено
     */
    suspend fun toggleFavouritePlaylist(playlistId: Long): Result<Boolean> {
        return try {
            val isFavourite = isPlaylistFavourite(playlistId)
            if (isFavourite) {
                removeFavouritePlaylist(playlistId).map { false }
            } else {
                addFavouritePlaylist(playlistId).map { true }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling favourite playlist", e)
            Result.failure(e)
        }
    }
}
