package com.example.myapplication.data

import com.example.myapplication.data.DatabaseFactory.Users
import com.example.myapplication.data.DatabaseFactory.Stations
import com.example.myapplication.User
import com.example.myapplication.Station
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

interface UserDataSource {
    fun createUser(user: User)
    fun findUserByUsername(username: String): User?

}


class StationDataSourceImpl {
    private fun rowToStation(row: ResultRow?) = if (row == null) null else Station(
        id = row[Stations.id],
        status = row[Stations.status],
        latitude = row[Stations.latitude],
        longitude = row[Stations.longitude]
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
                it[id] = station.id
                it[status] = station.status
                it[latitude] = station.latitude
                it[longitude] = station.longitude
            }
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
                it[password] = user.password // We'll store the hashed password here
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
}
