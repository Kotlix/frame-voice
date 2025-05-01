package ru.kotlix.frame.voice.server.repository

import ru.kotlix.frame.voice.server.repository.dto.ServerEntity

interface ServerRepository {
    fun save(entity: ServerEntity): ServerEntity

    fun findById(id: Long): ServerEntity?

    fun findAllByNameAndRegion(
        name: String,
        region: String,
    ): List<ServerEntity>
}
