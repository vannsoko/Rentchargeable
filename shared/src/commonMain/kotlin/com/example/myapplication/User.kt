package com.example.myapplication

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Int,
                val username: String,
                val password: String,
    )

@Serializable
data class UserCredentials(val username: String, val password: String)
