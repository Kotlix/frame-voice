package ru.kotlix.frame.voice.api.dto

data class JoinRequest(
    val userId: Long,
    val voiceId: Long,
    val serverName: String,
    val serverRegion: String,
)
