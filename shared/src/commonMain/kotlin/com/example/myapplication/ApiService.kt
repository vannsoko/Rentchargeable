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

    suspend fun addCarToUser(car: String, token: String): HttpResponse {
        return client.post("$baseUrl/user/addCar") {
            bearerAuth(token)
            setBody(car)
        }
    }

    // list stations
    suspend fun listStations(): HttpResponse {
        return client.post("$baseUrl/stations/{pos}")
    }


    suspend fun updateStationStatus(id: Int, token: String): HttpResponse {
        return client.post("$baseUrl/station/$id") {
            bearerAuth(token)
        }
    }

    // to call a lot in front-end
    suspend fun isTokenValid(token: String): Boolean {
        return (!isTokenExpired(token))
    }

    // /station/create/{long}/{lat}
    suspend fun createStation(propertyDetails: Station): HttpResponse {
        var long = propertyDetails.longitude
        var lat = propertyDetails.latitude
        return client.post("$baseUrl/station/create/$long/$lat")
    }

    suspend fun getAvailableStations(carValue: String): HttpResponse {
        return client.post(baseUrl)
    }

    // /station/delete/{id}
    suspend fun deleteStation(): HttpResponse {
        return client.post(baseUrl)
    }
}
