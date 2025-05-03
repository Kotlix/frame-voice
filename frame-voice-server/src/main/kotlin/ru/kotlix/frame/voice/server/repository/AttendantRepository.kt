package ru.kotlix.frame.voice.server.repository

import ru.kotlix.frame.voice.server.repository.dto.AttendantEntity

interface AttendantRepository {
    fun save(entity: AttendantEntity): AttendantEntity

    fun findById(id: Long): AttendantEntity?

    fun findAllByVoiceSessionId(voiceSessionId: Long): List<AttendantEntity>

    fun findByUserId(userId: Long): AttendantEntity?

    fun removeByUserId(userId: Long)
}
