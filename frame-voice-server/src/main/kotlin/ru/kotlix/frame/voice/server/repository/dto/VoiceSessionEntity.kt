package ru.kotlix.frame.voice.server.repository.dto

import java.time.OffsetDateTime

data class VoiceSessionEntity(
    val id: Long? = null,
    val createdAt: OffsetDateTime,
    val serverId: Long,
    val channelId: Long,
    val voiceId: Long,
    val secret: String,
)
