package ru.kotlix.frame.voice.server.repository.dto

import java.time.OffsetDateTime

data class AttendantEntity(
    val id: Long? = null,
    val joinedAt: OffsetDateTime,
    val userId: Long,
    val voiceSessionId: Long,
    val shadowId: Int,
)
