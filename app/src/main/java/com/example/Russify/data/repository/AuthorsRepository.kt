package com.example.Russify.data.repository

import com.example.Russify.data.network.ApiClient
import com.example.Russify.data.network.ApiErrorHandler
import com.example.Russify.data.network.AuthorDto
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Repository для работы с авторами
 */
class AuthorsRepository {
    private val client = ApiClient.client
    private val baseUrl = ApiClient.BASE_URL

    /**
     * Получить всех авторов
     */
    suspend fun getAllAuthors(): Result<List<AuthorDto>> {
        return ApiErrorHandler.safeApiCall {
            client.get("$baseUrl/authors").body()
        }
    }

    /**
     * Получить автора по ID
     *
     * @param id ID автора
     * @return Result с AuthorDto или ошибкой
     */
    suspend fun getAuthorById(id: Long): Result<AuthorDto> {
        return ApiErrorHandler.safeApiCall {
            client.get("$baseUrl/authors/$id").body()
        }
    }

    /**
     * Получить авторов по списку ID
     * (batch запрос для оптимизации)
     *
     * @param ids список ID авторов
     * @return Map<Long, AuthorDto> где ключ - ID автора
     */
    suspend fun getAuthorsByIds(ids: Set<Long>): Map<Long, AuthorDto> {
        if (ids.isEmpty()) return emptyMap()

        val authors = mutableMapOf<Long, AuthorDto>()

        // Загружаем авторов параллельно (можно оптимизировать через корутины)
        ids.forEach { id ->
            getAuthorById(id).onSuccess { author ->
                authors[id] = author
            }
        }

        return authors
    }

    /**
     * Получить имя автора по ID (для упрощенного использования)
     *
     * @param id ID автора
     * @return имя автора или "Unknown Artist" если не найден
     */
    suspend fun getAuthorName(id: Long): String {
        return getAuthorById(id).getOrNull()?.name ?: "Unknown Artist"
    }

    /**
     * Получить строку с именами авторов через запятую
     *
     * @param ids список ID авторов
     * @return строка вида "Artist1, Artist2, Artist3"
     */
    suspend fun getAuthorsNames(ids: Set<Long>): String {
        if (ids.isEmpty()) return "Unknown Artist"

        val authors = getAuthorsByIds(ids)
        return authors.values.joinToString(", ") { it.name }
    }
}
