package ru.kotlix.frame.voice.server.service.generator

interface IdGenerationService {
    fun generateSecret(): String

    fun generateChannelId(existing: List<Long>): Long

    fun generateShadowId(existing: List<Int>): Int
}
