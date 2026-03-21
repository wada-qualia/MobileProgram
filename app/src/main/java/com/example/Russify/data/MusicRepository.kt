package com.example.Russify.data

import android.util.Log
import com.example.Russify.data.network.ApiClient
import com.example.Russify.data.network.TrackDto
import com.example.Russify.data.repository.AuthorsRepository
import com.example.Russify.data.repository.FavoritesRepository
import com.example.Russify.data.repository.PlaylistsRepository
import com.example.Russify.model.Album
import com.example.Russify.model.Playlist
import com.example.Russify.model.Track
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import androidx.compose.ui.graphics.Color

object MusicRepository {
    private val client = ApiClient.client
    private val authorsRepository = AuthorsRepository()
    private val favoritesRepository = FavoritesRepository()
    private val playlistsRepository = PlaylistsRepository()

    private const val TAG = "MusicRepository"

    private val coverColors = listOf(
        Color(0xFFE57373), Color(0xFFBA68C8), Color(0xFF4FC3F7),
        Color(0xFF81C784), Color(0xFFFFB74D)
    )

    private fun resolveAudioUrl(dto: TrackDto): String {
        return dto.audioUrl
            ?: dto.audioHash?.let { "${ApiClient.BASE_URL}/media/music/$it" }
            ?: ""
    }

    private fun resolveCoverUrl(dto: TrackDto): String? {
        return dto.coverUrl
            ?: dto.coverHash?.let { "${ApiClient.BASE_URL}/media/images/$it" }
    }

    /**
     * Загрузить все треки с сервера с полной интеграцией:
     * - Загружает имена авторов через AuthorsRepository
     * - Проверяет статус избранного через FavoritesRepository
     * - Обрабатывает ошибки централизованно
     */
    suspend fun loadTracksFromServer(): List<Track> {
        return try {
            val response = client.get("tracks")

            if (response.status == HttpStatusCode.OK) {
                val dtoList: List<TrackDto> = response.body()

                val favouriteTracks = favoritesRepository.getFavouriteTracks()
                    .getOrNull()
                    ?.map { it.id }
                    ?.toSet()
                    ?: emptySet()

                val allAuthorIds = dtoList.flatMap { it.authorIds }.toSet()
                val authorsMap = if (allAuthorIds.isNotEmpty()) {
                    authorsRepository.getAuthorsByIds(allAuthorIds)
                } else {
                    emptyMap()
                }

                dtoList.mapIndexed { index, dto ->
                    val artistName = if (dto.authorIds.isNotEmpty()) {
                        dto.authorIds.mapNotNull { authorId ->
                            authorsMap[authorId]?.name
                        }.joinToString(", ").ifEmpty { "Unknown Artist" }
                    } else {
                        "Unknown Artist"
                    }

                    Track(
                        id = dto.id,
                        title = dto.name,
                        artist = artistName,
                        durationSeconds = 180,
                        audioUrl = resolveAudioUrl(dto),
                        coverUrl = resolveCoverUrl(dto),
                        genreId = dto.genreId ?: 0,
                        coverColor = coverColors[index % coverColors.size],
                        isFavorite = dto.id in favouriteTracks
                    )
                }
            } else {
                Log.e(TAG, "Failed to load tracks: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading tracks from server", e)
            emptyList()
        }
    }


    // Кэш для плейлистов и альбомов
    private val _playlists = mutableListOf<Playlist>()
    private val _albums = mutableListOf<Album>()

    /**
     * Загрузить плейлисты с сервера
     * (конвертирует PlaylistDto в Playlist модель)
     */
    suspend fun loadPlaylistsFromServer(): List<Playlist> {
        return try {
            val playlistDtos = playlistsRepository.getAllPlaylists()
                .getOrNull() ?: emptyList()

            playlistDtos.map { dto ->
                Playlist(
                    id = dto.id,
                    title = dto.name,
                    authorName = "User ${dto.userId}", // TODO: загрузить username через UserRepository
                    isSystem = dto.isSystem,
                    tracks = mutableListOf() // Треки загружаются отдельно при необходимости
                )
            }.also {
                _playlists.clear()
                _playlists.addAll(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading playlists from server", e)
            emptyList()
        }
    }

    suspend fun loadAlbumsFromServer(): List<Album> {
        return try {
            val albumDtos: List<com.example.Russify.data.network.AlbumDto> = client.get("albums").body()

            albumDtos.map { dto ->
                Album(
                    id = dto.id,
                    title = dto.title,
                    authorName = dto.authors?.firstOrNull()?.name ?: "Unknown Artist",
                    tracks = emptyList()
                )
            }.also {
                _albums.clear()
                _albums.addAll(it)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading albums from server", e)
            emptyList()
        }
    }

    /**
     * Получить плейлисты (возвращает кэш)
     * Для обновления вызовите loadPlaylistsFromServer()
     */
    fun getPlaylists(): List<Playlist> = _playlists

    /**
     * Получить альбомы (возвращает кэш)
     */
    fun getAlbums(): List<Album> = _albums

    /**
     * Создать новый плейлист локально (без сервера)
     * Для синхронной совместимости с существующим кодом
     *
     * @param name название плейлиста
     * @param author автор
     * @return созданный Playlist
     */
    fun createPlaylist(name: String, author: String): Playlist {
        val newId = (_playlists.maxOfOrNull { it.id } ?: 200) + 1
        val newPlaylist = Playlist(
            id = newId,
            title = name,
            authorName = author,
            isSystem = false,
            tracks = mutableListOf()
        )
        _playlists.add(newPlaylist)
        return newPlaylist
    }

    /**
     * Создать новый плейлист на сервере (async версия)
     *
     * @param name название плейлиста
     * @param author автор (игнорируется, берется из токена на backend)
     * @return созданный Playlist или null при ошибке
     */
    suspend fun createPlaylistAsync(name: String, author: String): Playlist? {
        return try {
            val response = playlistsRepository.createPlaylist(
                name = name,
                isSystem = false
            ).getOrNull()

            response?.let { playlistResponse ->
                val newPlaylist = Playlist(
                    id = playlistResponse.id,
                    title = playlistResponse.name,
                    authorName = author, // используем переданного автора
                    isSystem = playlistResponse.isSystem,
                    tracks = mutableListOf()
                )
                _playlists.add(newPlaylist)
                newPlaylist
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating playlist", e)
            null
        }
    }

    /**
     * Добавить трек в плейлист локально (без сервера)
     * Для синхронной совместимости с существующим кодом
     *
     * @param playlistId ID плейлиста
     * @param track трек для добавления
     */
    fun addTrackToPlaylist(playlistId: Long, track: Track) {
        val playlist = _playlists.find { it.id == playlistId }
        if (playlist != null && playlist.tracks.none { it.id == track.id }) {
            playlist.tracks.add(track)
        }
    }

    /**
     * Добавить трек в плейлист на сервере (async версия)
     *
     * @param playlistId ID плейлиста
     * @param track трек для добавления
     * @return true если успешно, false при ошибке
     */
    suspend fun addTrackToPlaylistAsync(playlistId: Long, track: Track): Boolean {
        return try {
            val result = playlistsRepository.addTrackToPlaylist(
                playlistId = playlistId,
                trackId = track.id
            )

            if (result.isSuccess) {
                // Обновляем локальный кэш
                val playlist = _playlists.find { it.id == playlistId }
                if (playlist != null && playlist.tracks.none { it.id == track.id }) {
                    playlist.tracks.add(track)
                }
                true
            } else {
                Log.e(TAG, "Failed to add track to playlist: ${result.exceptionOrNull()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding track to playlist", e)
            false
        }
    }

    /**
     * Удалить трек из плейлиста на сервере
     *
     * @param playlistId ID плейлиста
     * @param trackId ID трека
     * @return true если успешно, false при ошибке
     */
    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long): Boolean {
        return try {
            val result = playlistsRepository.removeTrackFromPlaylist(
                playlistId = playlistId,
                trackId = trackId
            )

            if (result.isSuccess) {
                // Обновляем локальный кэш
                val playlist = _playlists.find { it.id == playlistId }
                playlist?.tracks?.removeAll { it.id == trackId }
                true
            } else {
                Log.e(TAG, "Failed to remove track from playlist: ${result.exceptionOrNull()}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error removing track from playlist", e)
            false
        }
    }

    /**
     * Переключить статус избранного для трека
     *
     * @param trackId ID трека
     * @return Result<Boolean> где true = добавлено, false = удалено
     */
    suspend fun toggleFavoriteTrack(trackId: Long): Result<Boolean> {
        return favoritesRepository.toggleFavouriteTrack(trackId)
    }

    suspend fun setTrackFavorite(trackId: Long, shouldBeFavorite: Boolean): Result<Boolean> {
        return if (shouldBeFavorite) {
            favoritesRepository.addFavouriteTrack(trackId).map { true }
        } else {
            favoritesRepository.deleteFavouriteTrack(trackId).map { false }
        }
    }
}
