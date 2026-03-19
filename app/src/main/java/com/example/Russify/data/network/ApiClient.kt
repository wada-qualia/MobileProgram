package com.example.Russify.data.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {
    // ЗАМЕНИ НА АДРЕС ТВОЕГО СЕРВЕРА! Если локально на эмуляторе, то 10.0.2.2:порт
    const val BASE_URL = "http://10.0.2.2:8080"

    var authToken: String? = null // Сюда сохраним токен после логина

    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Игнорировать лишние поля из JSON
                prettyPrint = true
            })
        }
        defaultRequest {
            url(BASE_URL)

            authToken?.let { token ->
                header("Authorization", "Bearer $token")
            }
        }
    }
}