package com.example.Russify.data.repository

import android.util.Log
import com.example.Russify.data.network.ApiClient
import com.example.Russify.data.network.ApiErrorHandler
import com.example.Russify.data.network.TrackFlatDto
import com.example.Russify.data.network.TrackSearchRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Repository для поиска треков
 */
class SearchRepository {
    private val client = ApiClient.client
    private val baseUrl = ApiClient.BASE_URL

    companion object {
        private const val TAG = "SearchRepository"
    }

    /**
     * Поиск треков по имени и/или жанрам
     *
     * @param name название трека для поиска (optional, может быть null)
     * @param genreIds список ID жанров для фильтрации (optional, может быть null)
     * @return Result с List<TrackFlatDto> или ошибкой
     */
    suspend fun searchTracks(
        name: String? = null,
        genreIds: List<Long>? = null
    ): Result<List<TrackFlatDto>> {
        return ApiErrorHandler.safeApiCall {
            client.post("$baseUrl/tracks/search") {
                contentType(ContentType.Application.Json)
                setBody(TrackSearchRequest(
                    name = name,
                    genreIds = genreIds
                ))
            }.body()
        }
    }

    /**
     * Поиск треков только по имени
     *
     * @param query строка для поиска
     * @return Result с List<TrackFlatDto> или ошибкой
     */
    suspend fun searchTracksByName(query: String): Result<List<TrackFlatDto>> {
        return searchTracks(name = query, genreIds = null)
    }

    /**
     * Поиск треков только по жанрам
     *
     * @param genreIds список ID жанров
     * @return Result с List<TrackFlatDto> или ошибкой
     */
    suspend fun searchTracksByGenres(genreIds: List<Long>): Result<List<TrackFlatDto>> {
        return searchTracks(name = null, genreIds = genreIds)
    }

    /**
     * Поиск треков по имени и жанрам (комбинированный поиск)
     *
     * @param query строка для поиска
     * @param genreIds список ID жанров для фильтрации
     * @return Result с List<TrackFlatDto> или ошибкой
     */
    suspend fun searchTracksAdvanced(
        query: String,
        genreIds: List<Long>
    ): Result<List<TrackFlatDto>> {
        return searchTracks(name = query, genreIds = genreIds)
    }
}
