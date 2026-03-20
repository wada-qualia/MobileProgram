package com.example.Russify.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.Russify.data.network.ApiClient

/**
 * TokenManager для управления JWT токеном аутентификации.
 * Использует SharedPreferences для персистентного хранения.
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "russify_auth"
        private const val KEY_JWT_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"

        @Volatile
        private var instance: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * JWT токен текущего пользователя
     */
    var token: String?
        get() = prefs.getString(KEY_JWT_TOKEN, null)
        set(value) {
            prefs.edit().putString(KEY_JWT_TOKEN, value).apply()
            // Автоматически обновляем токен в ApiClient
            ApiClient.authToken = value
        }

    /**
     * ID текущего пользователя
     */
    var userId: Long?
        get() {
            val id = prefs.getLong(KEY_USER_ID, -1L)
            return if (id == -1L) null else id
        }
        set(value) {
            if (value != null) {
                prefs.edit().putLong(KEY_USER_ID, value).apply()
            } else {
                prefs.edit().remove(KEY_USER_ID).apply()
            }
        }

    /**
     * Имя текущего пользователя
     */
    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        set(value) {
            prefs.edit().putString(KEY_USERNAME, value).apply()
        }

    /**
     * Email текущего пользователя
     */
    var email: String?
        get() = prefs.getString(KEY_EMAIL, null)
        set(value) {
            prefs.edit().putString(KEY_EMAIL, value).apply()
        }

    /**
     * Проверка, авторизован ли пользователь
     */
    fun isLoggedIn(): Boolean {
        return !token.isNullOrEmpty()
    }

    /**
     * Сохранить данные пользователя после успешной аутентификации
     */
    fun saveUser(token: String, userId: Long, username: String, email: String) {
        this.token = token
        this.userId = userId
        this.username = username
        this.email = email
    }

    /**
     * Очистить все данные пользователя (logout)
     */
    fun clear() {
        prefs.edit().clear().apply()
        ApiClient.authToken = null
    }

    /**
     * Инициализация токена в ApiClient при старте приложения
     */
    fun initializeApiClient() {
        ApiClient.authToken = token
    }
}
