package org.stash.project.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private val db by lazy {
        Database.connect("jdbc:sqlite:sample.db", driver = "org.sqlite.JDBC")
    }

    fun init() {
        transaction(db) {
            SchemaUtils.create(Articles)
        }
    }

    fun addArticle(
        url: String,
        title: String,
        content: String,
    ) {
        transaction(db) {
            Articles.insert {
                it[Articles.title] = title
                it[Articles.url] = url
                it[Articles.content] = content
            }
        }
    }

    fun getAllArticles(): List<String> =
        transaction(db) {
            Articles.selectAll().map { it[Articles.title] }
        }

    fun getContentByTitle(title: String): String =
        transaction(db) {
            Articles.select { Articles.title eq title }.map { it[Articles.content] }.firstOrNull() ?: "No Content Found"
        }
}
