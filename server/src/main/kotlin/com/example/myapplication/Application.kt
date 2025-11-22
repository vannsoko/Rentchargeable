package com.example.myapplication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import com.example.myapplication.data.DatabaseFactory
import com.example.myapplication.data.UserDataSourceImpl
import com.example.myapplication.routes.authRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*
import java.security.MessageDigest
import kotlin.text.Charsets

// --- Modèles de données (Data Classes) ---
@Serializable
data class TokenResponse(val token: String)

// Dans un vrai projet, mettez ça dans application.conf ou des variables d'env
const val JWT_SECRET = "mon-secret-super-securise"
const val ISSUER = "mon-serveur-ktor"
const val AUDIENCE = "mon-app-kmp"
const val REALM = "Access to API"

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}


fun String.sha256(): String {
    // 1. Convert the input string to a byte array using a specific charset (UTF-8)
    val bytes = this.toByteArray(Charsets.UTF_8)

    // 2. Get an instance of the hashing algorithm
    val md = MessageDigest.getInstance("SHA-256")

    // 3. Compute the hash digest (a byte array)
    val digest = md.digest(bytes)

    // 4. Convert the byte array to a hexadecimal string representation
    return digest.fold("") { str, byte -> str + "%02x".format(byte) }
}


fun Application.module() {
    DatabaseFactory.init()
    val userDataSource = UserDataSourceImpl()
    var id = 0
    // Install JSON serialization (to read the request body)
    install(ContentNegotiation) {
        json()
    }

    // Configure JWT Authentication
    install(Authentication) {
        jwt("auth-jwt") {
            realm = REALM
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .withAudience(AUDIENCE)
                    .withIssuer(ISSUER)
                    .build()
            )
            validate { credential ->
                // if token is valid -> authorised
                if (credential.payload.audience.contains(AUDIENCE)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "invalid or expired Token")
            }
        }
    }

    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        post("/login") {
            val user = call.receive<UserCredentials>()

            if (userDataSource.findUserByUsername(user.username)?.password == user.password.sha256()) {

                // Token generation
                val token = JWT.create()
                    .withAudience(AUDIENCE)
                    .withIssuer(ISSUER)
                    .withClaim("username", user.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 hours
                    .sign(Algorithm.HMAC256(JWT_SECRET))

                call.respond(HttpStatusCode.OK, TokenResponse(token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Incorrect credentials")
            }
        }

        post("/register") {
            val user = call.receive<UserCredentials>()
            // Check if already exists
            if (userDataSource.findUserByUsername(user.username) != null) {
                call.respond(HttpStatusCode.Conflict, "Username already exists")
                return@post
            }

            userDataSource.createUser(User(
                id = id,
                username = user.username,
                password = user.password.sha256()
            ))
            id += 1
            call.respond(HttpStatusCode.Created, "User registered successfully")
        }

        // Get the list of station in the range
        get("/stations/{pos}") {
            val pos = call.parameters["pos"]
            // TODO: get all the station in the range
        }

        // Secured API paths
        authenticate("auth-jwt") {
            // Get user data from the database
            get("/user") {
                //val token = call.parameters["token"]
                val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")

                // TODO: get the user data from the database
            }
            // Update station status by a given user
            put("/station/{id}") {
                val id = call.parameters["id"]
                //val token = call.parameters["token"]
                val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")
                // TODO: update the station data in the database
            }
            // Create a station
            post("/station/create/{id}") {
                val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")


            }
            // Delete a given station
            post("/station/delete/{id}") {
                val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")


            }
        }
    }
}
