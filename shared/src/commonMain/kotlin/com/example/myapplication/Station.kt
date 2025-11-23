package com.example.myapplication

import kotlinx.serialization.Serializable

@Serializable
data class Station(
    val id: Int,
    var status: Boolean,
    val latitude: String,
    val longitude: String,
    val userId: Int
)
