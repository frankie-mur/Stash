package org.stash.project.db

import org.jetbrains.exposed.sql.Table

object Articles : Table() {
    val id = integer("id").autoIncrement()
    val title = text("title")
    val url = text("url")
    val content = text("content")
    override val primaryKey = PrimaryKey(id)
}
