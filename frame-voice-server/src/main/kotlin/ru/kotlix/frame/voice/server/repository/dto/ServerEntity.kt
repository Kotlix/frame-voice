package ru.kotlix.frame.voice.server.repository.dto

import java.time.OffsetDateTime

data class ServerEntity(
    val id: Long? = null,
    val createdAt: OffsetDateTime,
    val name: String,
    val region: String,
    val hostAddress: String,
    val active: Boolean,
)
