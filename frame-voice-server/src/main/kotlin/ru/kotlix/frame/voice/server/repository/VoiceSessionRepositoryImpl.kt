package ru.kotlix.frame.voice.server.repository

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.kotlix.frame.voice.server.repository.dto.VoiceSessionEntity
import java.time.OffsetDateTime

@Repository
class VoiceSessionRepositoryImpl(
    private val npJdbc: NamedParameterJdbcTemplate,
) : VoiceSessionRepository {
    companion object {
        private val ROW_MAPPER =
            RowMapper { rs, _ ->
                VoiceSessionEntity(
                    id = rs.getLong("id"),
                    createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
                    serverId = rs.getLong("server_id"),
                    channelId = rs.getLong("channel_id"),
                    voiceId = rs.getLong("voice_id"),
                    secret = rs.getString("secret"),
                )
            }
    }

    override fun save(entity: VoiceSessionEntity): VoiceSessionEntity =
        npJdbc.queryForObject(
            """
            insert into voice_session
            (created_at, server_id, channel_id, voice_id, secret)
            values
            (:created_at, :server_id, :channel_id, :voice_id, :secret)
            returning *;
            """.trimIndent(),
            mapOf(
                "created_at" to entity.createdAt,
                "server_id" to entity.serverId,
                "channel_id" to entity.channelId,
                "voice_id" to entity.voiceId,
                "secret" to entity.secret,
            ),
            ROW_MAPPER,
        )!!

    override fun findById(id: Long): VoiceSessionEntity? =
        npJdbc.query(
            """
            select * from voice_session
            where id = :id;
            """.trimIndent(),
            mapOf("id" to id),
            ROW_MAPPER,
        ).firstOrNull()

    override fun findAllByServerId(serverId: Long): List<VoiceSessionEntity> =
        npJdbc.query(
            """
            select * from voice_session
            where server_id = :server_id;
            """.trimIndent(),
            mapOf("server_id" to serverId),
            ROW_MAPPER,
        )

    override fun findByVoiceId(voiceId: Long): VoiceSessionEntity? =
        npJdbc.query(
            """
            select * from voice_session
            where voice_id = :voice_id;
            """.trimIndent(),
            mapOf(
                "voice_id" to voiceId,
            ),
            ROW_MAPPER,
        ).firstOrNull()

    override fun removeById(id: Long) {
        npJdbc.update(
            """
            delete from voice_session
            where id = :id;
            """.trimIndent(),
            mapOf("id" to id),
        )
    }
}
