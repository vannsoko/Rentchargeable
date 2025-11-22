package com.example.myapplication.data

import com.example.myapplication.data.DatabaseFactory.Users
import com.example.myapplication.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

interface UserDataSource {
    fun createUser(user: User)
    fun findUserByUsername(username: String): User?

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
