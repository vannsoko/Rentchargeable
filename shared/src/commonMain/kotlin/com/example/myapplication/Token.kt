package com.example.myapplication

import com.sun.org.apache.xpath.internal.operations.Bool
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalEncodingApi::class, ExperimentalTime::class)
fun isTokenExpired(token: String): Boolean {
    try {
        val parts = token.split(".")
        if (parts.size != 3) return true // Invalid format

        // 1. Decode the Payload (the 2nd part)
        // JWTs use Base64 URL encoding, we must decode it
        val payloadJson = String(Base64.UrlSafe.decode(parts[1]))

        // 2. Parse JSON
        val jsonElement = Json.parseToJsonElement(payloadJson)
        val expSeconds = jsonElement.jsonObject["exp"]?.jsonPrimitive?.long ?: return true

        // 3. Compare with Current Time
        // 'exp' is in Seconds, System.now is in Milliseconds usually
        val currentSeconds = Clock.System.now().epochSeconds

        // Return true if current time is AFTER expiration
        // We add a 10-second buffer to be safe against clock skew
        val bool = currentSeconds.compareTo((expSeconds - 10))
        return bool > 0

    } catch (e: Exception) {
        return true // If we can't parse it, assume it's bad
    }
}
