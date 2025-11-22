package com.example.myapplication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
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

// --- Modèles de données (Data Classes) ---
@Serializable
data class UserCredentials(val username: String, val password: String)

@Serializable
data class User(val username: String, val password: String, )


@Serializable
data class TokenResponse(val token: String)

// --- Configuration ---
// Dans un vrai projet, mettez ça dans application.conf ou des variables d'env
const val SECRET = "mon-secret-super-securise"
const val ISSUER = "mon-serveur-ktor"
const val AUDIENCE = "mon-app-kmp"
const val REALM = "Access to API"

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 1. Installer la sérialisation JSON (pour lire le body des requêtes)
    install(ContentNegotiation) {
        json()
    }

    // 2. Configurer l'Authentification JWT
    install(Authentication) {
        jwt("auth-jwt") {
            realm = REALM
            verifier(
                JWT.require(Algorithm.HMAC256(SECRET))
                    .withAudience(AUDIENCE)
                    .withIssuer(ISSUER)
                    .build()
            )
            validate { credential ->
                // Si le token contient une audience valide, on autorise
                if (credential.payload.audience.contains(AUDIENCE)) {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "invalid or expired Token")
            }
        }
    }

    // 3. Définir les Routes
    routing {
        post("/login") {
            val user = call.receive<UserCredentials>()

            // Simuler une vérification en base de données
            // ICI : Remplacez par votre propre logique (BDD, Hachage mot de passe)
            // TODO: Implement authentification with databank
            if (user.username == "admin" && user.password == "password123") {

                // Génération du token
                val token = JWT.create()
                    .withAudience(AUDIENCE)
                    .withIssuer(ISSUER)
                    .withClaim("username", user.username) // On stocke le nom dans le token
                    .withExpiresAt(Date(System.currentTimeMillis() + 86400000)) // 24 heure
                    .sign(Algorithm.HMAC256(SECRET))

                call.respond(TokenResponse(token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Incorrect credentials")
            }
        }

        post("/signup") {
            val user = call.receive<UserCredentials>()
            // TODO: create an input in the database

        }
        // Get the list of station in the range
        get("/stations/{distance}/{pos}") {
            val distance = call.parameters["distance"]
            val pos = call.parameters["pos"]
            // TODO: get all the station in the range
        }

        // --- Route Sécurisée ---
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





            get("/api/me") {
                // Récupérer les infos contenues dans le token
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val expiresAt = principal.payload.expiresAt
                val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")
                call.respondText("$token")
                call.respondText("Bonjour $username ! Vous avez accès aux données sécurisées. (Token expire : $expiresAt)")
            }
        }
    }
}