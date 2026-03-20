package com.example.Russify.data.repository

import android.util.Log
import com.example.Russify.data.local.TokenManager
import com.example.Russify.data.network.ApiClient
import com.example.Russify.data.network.AuthResponse
import com.example.Russify.data.network.LoginRequest
import com.example.Russify.data.network.RegisterRequest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Repository для работы с аутентификацией.
 * Обрабатывает регистрацию, вход и выход пользователей.
 */
class AuthRepository(
    private val tokenManager: TokenManager
) {
    private val client = ApiClient.client
    private val baseUrl = ApiClient.BASE_URL

    companion object {
        private const val TAG = "AuthRepository"
    }

    /**
     * Регистрация нового пользователя
     *
     * @param username Имя пользователя
     * @param email Email
     * @param password Пароль (минимум 8 символов)
     * @return Result с AuthResponse или ошибкой
     */
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<AuthResponse> {
        return try {
            Log.d(TAG, "Registering user: $username, $email")

            val response: AuthResponse = client.post("$baseUrl/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(username, email, password))
            }.body()

            Log.d(TAG, "Registration successful: ${response.username}")

            // Сохранить токен и данные пользователя
            tokenManager.saveUser(
                token = response.token,
                userId = response.id,
                username = response.username,
                email = response.email
            )

            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Registration error", e)
            Result.failure(e)
        }
    }

    /**
     * Вход пользователя
     *
     * @param email Email
     * @param password Пароль
     * @return Result с AuthResponse или ошибкой
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<AuthResponse> {
        return try {
            Log.d(TAG, "Logging in: $email")

            val response: AuthResponse = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }.body()

            Log.d(TAG, "Login successful: ${response.username}")

            // Сохранить токен и данные пользователя
            tokenManager.saveUser(
                token = response.token,
                userId = response.id,
                username = response.username,
                email = response.email
            )

            Result.success(response)
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            Result.failure(e)
        }
    }

    /**
     * Выход пользователя
     *
     * @return Result с Unit или ошибкой
     */
    suspend fun logout(): Result<Unit> {
        return try {
            Log.d(TAG, "Logging out")

            // Отправить запрос на сервер для invalidation токена
            client.post("$baseUrl/auth/logout") {
                contentType(ContentType.Application.Json)
            }

            Log.d(TAG, "Logout successful")

            // Очистить локальные данные
            tokenManager.clear()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Logout error", e)
            // Даже если запрос на сервер упал, очистим локальные данные
            tokenManager.clear()
            Result.failure(e)
        }
    }

    /**
     * Проверка, авторизован ли пользователь
     */
    fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    /**
     * Получить текущее имя пользователя
     */
    fun getCurrentUsername(): String? {
        return tokenManager.username
    }

    /**
     * Получить текущий email пользователя
     */
    fun getCurrentEmail(): String? {
        return tokenManager.email
    }
}
