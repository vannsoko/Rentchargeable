package com.example.rentchargeable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform