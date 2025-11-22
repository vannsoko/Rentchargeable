package com.example.myapplication

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class ApiService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    // Use 10.0.2.2 for Android emulator to connect to localhost
    private val baseUrl = "http://0.0.0.0:8080"

    suspend fun register(user: User): HttpResponse {
        return client.post("$baseUrl/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    suspend fun login(user: User): HttpResponse {
        return client.post("$baseUrl/login") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }
}
