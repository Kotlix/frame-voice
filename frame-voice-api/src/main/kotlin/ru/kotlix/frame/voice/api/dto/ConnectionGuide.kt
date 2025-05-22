package ru.kotlix.frame.voice.api.dto

data class ConnectionGuide(
    val hostAddress: String,
    val secret: String,
    val channelId: Long,
    val shadowId: Int,
)
