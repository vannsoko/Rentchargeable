package com.example.myapplication.routes

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.myapplication.data.UserDataSource
import com.example.myapplication.User
import io.ktor.http.*
// import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(userDataSource: UserDataSource) {

    post("/register") {
        val user = call.receive<User>()

        // Check if already exists
        if (userDataSource.findUserByUsername(user.username) != null) {
            call.respond(HttpStatusCode.Conflict, "Username already exists")
            return@post
        }

        // Hash the password and create the new user
        val hashedPassword = BCrypt.withDefaults().hashToString(12, user.password.toCharArray())
        val newUser = user.copy(password = hashedPassword)
        userDataSource.createUser(newUser)

        call.respond(HttpStatusCode.Created, "User registered successfully")
        }

        post("/login") {
            val userCredentials = call.receive<User>()

            // Find user by username
            val foundUser = userDataSource.findUserByUsername(userCredentials.username)
            if (foundUser == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                return@post
            }

            // Verify password
            val isPasswordCorrect = BCrypt.verifyer()
                .verify(userCredentials.password.toCharArray(), foundUser.password)

            if (isPasswordCorrect.verified) {
                call.respond(HttpStatusCode.OK, "Login successful")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }
    }