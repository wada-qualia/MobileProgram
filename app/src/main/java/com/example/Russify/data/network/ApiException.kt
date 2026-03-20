package com.example.Russify.data.network

/**
 * Базовый класс для всех API ошибок
 */
sealed class ApiException(message: String) : Exception(message) {

    /**
     * Ошибка 401 - Unauthorized (не авторизован или токен истёк)
     */
    class Unauthorized(message: String = "Unauthorized. Please login again.") : ApiException(message)

    /**
     * Ошибка 403 - Forbidden (нет прав доступа)
     */
    class Forbidden(message: String = "Access forbidden") : ApiException(message)

    /**
     * Ошибка 404 - Not Found (ресурс не найден)
     */
    class NotFound(message: String = "Resource not found") : ApiException(message)

    /**
     * Ошибка 409 - Conflict (конфликт данных, например пользователь уже существует)
     */
    class Conflict(message: String = "Data conflict") : ApiException(message)

    /**
     * Ошибка 422 - Unprocessable Entity (невалидные данные)
     */
    class ValidationError(message: String = "Validation error") : ApiException(message)

    /**
     * Ошибка 5xx - Server Error
     */
    class ServerError(message: String = "Server error. Please try again later.") : ApiException(message)

    /**
     * Ошибка сети (timeout, no connection, etc)
     */
    class NetworkError(message: String = "Network error. Check your connection.") : ApiException(message)

    /**
     * Неизвестная ошибка
     */
    class Unknown(message: String = "Unknown error occurred") : ApiException(message)
}
