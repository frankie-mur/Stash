package org.stash.project.domain

import java.time.LocalDateTime

data class Article(
    val id: Long = 0,
    val title: String,
    val url: String,
    val dateAdded: LocalDateTime = LocalDateTime.now(),
    val isRead: Boolean = false,
    val tags: List<String> = emptyList()
)