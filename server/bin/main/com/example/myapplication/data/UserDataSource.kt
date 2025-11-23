package com.example.myapplication.data

import com.example.myapplication.data.DatabaseFactory.Users
import com.example.myapplication.data.DatabaseFactory.Stations
import com.example.myapplication.User
import com.example.myapplication.Station
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.*

interface UserDataSource {
    fun createUser(user: User)
    fun findUserByUsername(username: String): User?
    fun addCarToUser(userId: Int, car: String)
}

class StationDataSourceImpl {
    private fun rowToStation(row: ResultRow?) = if (row == null) null else Station(
        id = row[Stations.id],
        status = row[Stations.status],
        latitude = row[Stations.latitude].toString(),
        longitude = row[Stations.longitude].toString(),
        userId = row[Stations.userId]
    )

      fun findStationById(id: Int): Station? {
        return transaction {
            Stations.select { Stations.id eq id }
                .map(::rowToStation)
                .singleOrNull()
        }
    }

     fun createStation(station: Station) {
        return transaction {
            Stations.insert {
                it[status] = station.status
                it[latitude] = station.latitude.toDouble()
                it[longitude] = station.longitude.toDouble()
                it[userId] = station.userId
            }
        }
    }

fun deleteStation(id: Int) {
    return transaction{
        Stations.deleteWhere { Stations.id eq id }
    }
}

    fun updateStationStatus(id: Int, newStatus: Boolean) {
        findStationById(id)?.status = newStatus
    }
}


class UserDataSourceImpl : UserDataSource {
    private fun rowToUser(row: ResultRow?) = if (row == null) null else User(
        id = row[Users.id],
        username = row[Users.username],
        password = row[Users.password]
    )

    override fun createUser(user: User) {
        transaction {
            Users.insert {
                it[username] = user.username
                it[password] = user.password
                    it[carCount] = 0      // ensure carCount is initialized 
                    it[cars] = ""         // ensure cars is initialized
            }
        }
    }

    override fun findUserByUsername(username: String): User? {
        return transaction {
            Users.select { Users.username eq username }
                .map(::rowToUser)
                .singleOrNull()
        }
    }

    override fun addCarToUser(userId: Int, car: String) {
        transaction {
            val userRow = Users.select { Users.id eq userId }.singleOrNull()
            if (userRow != null) {
                val currentCount = userRow[Users.carCount]
                val currentCars = userRow[Users.cars]
                val updatedCars = if (currentCars.isEmpty()) car else "$currentCars,$car"
                Users.update({ Users.id eq userId }) {
                    it[Users.carCount] = currentCount + 1
                    it[Users.cars] = updatedCars
                }
            }
        }
    }
}
