package com.example.myapplication.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    object Stations : Table() {
        val id = integer("id").autoIncrement()
        var status = bool("status")
        val latitude = double("latitude")
        val longitude = double("longitude")
        override val primaryKey = PrimaryKey(Users.id)
    }
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val username = varchar("username", 128).uniqueIndex()
        val password = varchar("password", 64)
        
        override val primaryKey = PrimaryKey(id)
    }

    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1" // In-memory database
        val database = Database.connect(jdbcURL, driverClassName)

        transaction(database) {
            SchemaUtils.create(Users)
            SchemaUtils.create(Stations)
        }
    }
}
