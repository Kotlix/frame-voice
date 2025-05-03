package ru.kotlix.frame.voice.server.repository

import ru.kotlix.frame.voice.server.repository.dto.VoiceSessionEntity

interface VoiceSessionRepository {
    fun save(entity: VoiceSessionEntity): VoiceSessionEntity

    fun findById(id: Long): VoiceSessionEntity?

    fun findAllByServerId(serverId: Long): List<VoiceSessionEntity>

    fun findByVoiceId(voiceId: Long): VoiceSessionEntity?

    fun removeById(id: Long)
}
