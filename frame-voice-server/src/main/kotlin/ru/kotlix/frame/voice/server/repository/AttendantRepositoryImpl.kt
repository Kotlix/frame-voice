package ru.kotlix.frame.voice.server.repository

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.kotlix.frame.voice.server.repository.dto.AttendantEntity
import java.time.OffsetDateTime

@Repository
class AttendantRepositoryImpl(
    private val npJdbc: NamedParameterJdbcTemplate,
) : AttendantRepository {
    companion object {
        private val ROW_MAPPER =
            RowMapper { rs, _ ->
                AttendantEntity(
                    id = rs.getLong("id"),
                    joinedAt = rs.getObject("joined_at", OffsetDateTime::class.java),
                    userId = rs.getLong("user_id"),
                    voiceSessionId = rs.getLong("voice_session_id"),
                    shadowId = rs.getInt("shadow_id"),
                )
            }
    }

    override fun save(entity: AttendantEntity): AttendantEntity =
        npJdbc.queryForObject(
            """
            insert into attendant
            (joined_at, user_id, voice_session_id, shadow_id)
            values
            (:joined_at, :user_id, :voice_session_id, :shadow_id)
            returning *;
            """.trimIndent(),
            mapOf(
                "joined_at" to entity.joinedAt,
                "user_id" to entity.userId,
                "voice_session_id" to entity.voiceSessionId,
                "shadow_id" to entity.shadowId,
            ),
            ROW_MAPPER,
        )!!

    override fun findById(id: Long): AttendantEntity? =
        npJdbc.query(
            """
            select * from attendant
            where id = :id;
            """.trimIndent(),
            mapOf("id" to id),
            ROW_MAPPER,
        ).firstOrNull()

    override fun findAllByVoiceSessionId(voiceSessionId: Long): List<AttendantEntity> =
        npJdbc.query(
            """
            select * from attendant
            where voice_session_id = :voice_session_id;
            """.trimIndent(),
            mapOf("voice_session_id" to voiceSessionId),
            ROW_MAPPER,
        )

    override fun findByUserId(userId: Long): AttendantEntity? =
        npJdbc.query(
            """
            select * from attendant
            where user_id = :user_id;
            """.trimIndent(),
            mapOf("user_id" to userId),
            ROW_MAPPER,
        ).firstOrNull()

    override fun removeByUserId(userId: Long) {
        npJdbc.update(
            """
            delete from attendant
            where user_id = :user_id;
            """.trimIndent(),
            mapOf("user_id" to userId),
        )
    }
}
