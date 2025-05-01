package ru.kotlix.frame.voice.server.repository

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.kotlix.frame.voice.server.repository.dto.ServerEntity
import java.time.OffsetDateTime

@Repository
class ServerRepositoryImpl(
    private val npJdbc: NamedParameterJdbcTemplate,
) : ServerRepository {
    companion object {
        private val ROW_MAPPER =
            RowMapper { rs, _ ->
                ServerEntity(
                    id = rs.getLong("id"),
                    createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
                    name = rs.getString("name"),
                    region = rs.getString("region"),
                    hostAddress = rs.getString("host_address"),
                    active = rs.getBoolean("active"),
                )
            }
    }

    override fun save(entity: ServerEntity): ServerEntity =
        npJdbc.queryForObject(
            """
            insert into server
            (created_at, name, region, host_address, active)
            values
            (:created_at, :name, :region, :host_address, :active)
            returning *;
            """.trimIndent(),
            mapOf(
                "created_at" to entity.createdAt,
                "name" to entity.name,
                "region" to entity.region,
                "host_address" to entity.hostAddress,
                "active" to entity.active,
            ),
            ROW_MAPPER,
        )!!

    override fun findById(id: Long): ServerEntity? =
        npJdbc.query(
            """
            select * from server
            where id = :id;
            """.trimIndent(),
            mapOf("id" to id),
            ROW_MAPPER,
        ).firstOrNull()

    override fun findAllByNameAndRegion(
        name: String,
        region: String,
    ): List<ServerEntity> =
        npJdbc.query(
            """
            select * from server
            where name = :name
                and region = :region;
            """.trimIndent(),
            mapOf(
                "name" to name,
                "region" to region,
            ),
            ROW_MAPPER,
        )
}
