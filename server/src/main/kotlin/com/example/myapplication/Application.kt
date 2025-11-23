package com.example.myapplication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import com.example.myapplication.data.DatabaseFactory
import com.example.myapplication.data.UserDataSourceImpl
import com.example.myapplication.data.StationDataSourceImpl
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

@Serializable
data class CarRequest(val carValue: String, val username: String)

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
    val stationDataSource = StationDataSourceImpl()
    var count_usr_id = 0
    var count_station_id = 0

    // Install JSON serialization (to read the request body)
    install(ContentNegotiation) {
        json()
    }


    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        post("/login") {
            val user = call.receive<UserCredentials>()
            if (userDataSource.findUserByUsername(user.username)?.password == user.password.sha256()) {
                call.respond(HttpStatusCode.OK, TokenResponse(token = "dummy-token"))
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
                id = count_usr_id,
                username = user.username,
                password = user.password.sha256()
            ))
            count_usr_id += 1
            call.respond(HttpStatusCode.Created, "User registered successfully")
        }

        // Get the list of station in the range
        get("/stations/{pos}") {
            val pos = call.parameters["pos"]
            // TODO: get all the station in the range
        }

        get("/user") {
            // Don't check token anymore
            // val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")
            // TODO: get the user data from the database
        }
        // Update station status by a given user
        put("/station/{id}") {
            // val token = call.request.headers["Authorization"]?.substringAfter("Bearer ")
            // TODO: update the station data in the database
        }
        // Create a station
        post("/station/create/{long}/{lat}") {
            val long: String = call.parameters["long"].toString()
            val lat: String = call.parameters["lat"].toString()

            // No principal/token use!
            // val principal = call.principal<JWTPrincipal>()
            // val username = principal!!.payload.getClaim("username").asString()
            // val usrId = userDataSource.findUserByUsername(username)?.id
            val usrId = 0 // Or pick a default/dummy user for now
            stationDataSource.createStation(Station(count_station_id, false, lat, long, usrId))
            count_station_id += 1
            call.respond(HttpStatusCode.Created, "Station has been created")
        }
        // Delete a given station
        post("/station/delete/{id}") {
            // Don't check principal
            val usr_id = 0 // Or pick dummy for now
            stationDataSource.deleteStation( usr_id )
            call.respond(HttpStatusCode.OK, "Station has been deleted")
        }

        post("/user/addCar") {
            // No principal/token!
            val req = call.receive<CarRequest>()
            val username = req.username
            val user = userDataSource.findUserByUsername(username)
            if (user != null) {
                userDataSource.addCarToUser(user.id, req.carValue)
                call.respond(HttpStatusCode.OK, "Car added")
            } else {
                call.respond(HttpStatusCode.Unauthorized, "User not found")
            }
        }
    }
}
