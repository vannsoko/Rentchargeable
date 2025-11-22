package com.example.myapplication

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.*


class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Ktor: ${Greeting().greet()}", response.bodyAsText())
    }

    @Test
    fun testRegister() = testApplication {
        // Create a test client that can handle JSON
        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        application {
            module()
        }

        // --- Test 1: Successful Registration ---
        val response = client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(User(id = 0, username = "testuser", password = "password123"))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals("User registered successfully", response.bodyAsText())

        // --- Test 2: Username Conflict ---
        // Try to register the same user again
        val conflictResponse = client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(User(id = 0, username = "testuser", password = "anotherpassword"))
        }
        assertEquals(HttpStatusCode.Conflict, conflictResponse.status)
        assertEquals("Username already exists", conflictResponse.bodyAsText())
    }

        @Test
        fun testLogin() = testApplication {
            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }
            application { module() }

            val testUser = User(id = 0, username = "logintester", password = "password123")

            // Register the user first
            client.post("/register") {
                contentType(ContentType.Application.Json)
                setBody(testUser)
            }

            // --- Test 1: Successful Login ---
            val loginResponse = client.post("/login") {
                contentType(ContentType.Application.Json)
                setBody(testUser)
            }
            assertEquals(HttpStatusCode.OK, loginResponse.status)
            assertEquals("Login successful", loginResponse.bodyAsText())

            // --- Test 2: Failed Login (Wrong Password) ---
            val wrongPasswordResponse = client.post("/login") {
                contentType(ContentType.Application.Json)
                setBody(User(id = 0, username = testUser.username, password = "wrongpassword"))
            }
            assertEquals(HttpStatusCode.Unauthorized, wrongPasswordResponse.status)
            assertEquals("Invalid credentials", wrongPasswordResponse.bodyAsText())

            // --- Test 3: Failed Login (User Not Found) ---
            val nonExistentUserResponse = client.post("/login") {
                contentType(ContentType.Application.Json)
                setBody(User(id = 0, username = "nosuchuser", password = "password"))
            }
            assertEquals(HttpStatusCode.Unauthorized, nonExistentUserResponse.status)
            assertEquals("Invalid credentials", nonExistentUserResponse.bodyAsText())
        }
    }
