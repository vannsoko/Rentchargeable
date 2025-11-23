package com.example.myapplication

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

@Serializable
data class CarRequest(val carValue: String, val username: String)

class ApiService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    // Use 10.0.2.2 for Android emulator to connect to localhost
    private val baseUrl = "http://10.0.2.2:8080"

    suspend fun register(user: UserCredentials): HttpResponse {
        return client.post("$baseUrl/register") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    // login page
    suspend fun login(user: UserCredentials): HttpResponse {
        return client.post("$baseUrl/login") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    suspend fun addCarToUser(car: String, name: String, token: String): HttpResponse {
        return client.post("$baseUrl/user/addCar") {
            contentType(ContentType.Application.Json)    // <-- Add this!
            // setBody(car)                              // Don't send just the string
            setBody(CarRequest(
                carValue = car,
                username = name
            ))          // Send as JSON object
        }
    }


    // /station/create/{long}/{lat}
    suspend fun createStation(propertyDetails: Station): HttpResponse {
        val long = propertyDetails.longitude
        val lat = propertyDetails.latitude
        return client.post("$baseUrl/station/create/$long/$lat")
    }
}
