package com.example.Russify.data.network

import android.util.Log
import io.ktor.client.plugins.*
import io.ktor.utils.io.errors.*
import java.net.UnknownHostException

/**
 * Обработчик ошибок API.
 * Конвертирует HTTP и сетевые ошибки в ApiException.
 */
object ApiErrorHandler {

    private const val TAG = "ApiErrorHandler"

    /**
     * Обёртка для безопасного выполнения API запроса.
     * Перехватывает исключения и конвертирует в ApiException.
     *
     * @param call suspend функция с API запросом
     * @return Result с результатом или ApiException
     */
    suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> {
        return try {
            Result.success(call())
        } catch (e: Exception) {
            Log.e(TAG, "API call failed", e)
            Result.failure(mapException(e))
        }
    }

    /**
     * Маппинг исключений в ApiException
     */
    private fun mapException(e: Exception): ApiException {
        return when (e) {
            // Ktor HTTP ошибки
            is ClientRequestException -> {
                when (e.response.status.value) {
                    401 -> ApiException.Unauthorized("Authentication required or token expired")
                    403 -> ApiException.Forbidden("Access to this resource is forbidden")
                    404 -> ApiException.NotFound("Resource not found")
                    409 -> ApiException.Conflict("Resource already exists or conflict")
                    422 -> ApiException.ValidationError("Invalid data provided")
                    in 400..499 -> ApiException.ValidationError("Client error: ${e.message}")
                    else -> ApiException.Unknown("HTTP error: ${e.response.status.value}")
                }
            }

            // Ошибки сервера (5xx)
            is ServerResponseException -> {
                ApiException.ServerError("Server error: ${e.response.status.value}. Please try again later.")
            }

            // Timeout
            is HttpRequestTimeoutException -> {
                ApiException.NetworkError("Request timeout. Check your internet connection.")
            }

            // Сетевые ошибки
            is UnknownHostException -> {
                ApiException.NetworkError("Cannot connect to server. Check your internet connection.")
            }

            is IOException -> {
                ApiException.NetworkError("Network error: ${e.message}")
            }

            // Уже ApiException - просто вернуть
            is ApiException -> e

            // Всё остальное
            else -> {
                ApiException.Unknown("Unexpected error: ${e.message}")
            }
        }
    }

    /**
     * Получить читаемое сообщение об ошибке для пользователя
     *
     * @param exception ApiException
     * @param isRussian язык (true = русский, false = английский)
     * @return строка с сообщением для пользователя
     */
    fun getUserMessage(exception: ApiException, isRussian: Boolean = true): String {
        return if (isRussian) {
            when (exception) {
                is ApiException.Unauthorized -> "Требуется авторизация. Войдите снова."
                is ApiException.Forbidden -> "Доступ запрещён"
                is ApiException.NotFound -> "Ресурс не найден"
                is ApiException.Conflict -> "Данные уже существуют"
                is ApiException.ValidationError -> "Неверные данные: ${exception.message}"
                is ApiException.ServerError -> "Ошибка сервера. Попробуйте позже."
                is ApiException.NetworkError -> "Ошибка сети. Проверьте подключение."
                is ApiException.Unknown -> "Неизвестная ошибка"
            }
        } else {
            when (exception) {
                is ApiException.Unauthorized -> "Authorization required. Please login again."
                is ApiException.Forbidden -> "Access forbidden"
                is ApiException.NotFound -> "Resource not found"
                is ApiException.Conflict -> "Data already exists"
                is ApiException.ValidationError -> "Invalid data: ${exception.message}"
                is ApiException.ServerError -> "Server error. Try again later."
                is ApiException.NetworkError -> "Network error. Check your connection."
                is ApiException.Unknown -> "Unknown error"
            }
        }
    }
}
