package com.example.myapplication

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val password: String,
    val car_count: Int = 0,
    val cars: String = ""
)

@Serializable
data class UserCredentials(val username: String, val password: String)
