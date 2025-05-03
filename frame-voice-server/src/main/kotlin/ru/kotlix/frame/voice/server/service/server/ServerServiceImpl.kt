package ru.kotlix.frame.voice.server.service.server

import org.springframework.stereotype.Service
import ru.kotlix.frame.voice.server.repository.ServerRepository

@Service
class ServerServiceImpl(
    private val serverRepository: ServerRepository,
) : ServerService {
    override fun getServersGroupedByRegion(): Map<String, List<String>> {
        val allServers = serverRepository.findAll()

        val grouped =
            allServers.filter { it.active }.groupBy { it.region }
                .mapValues { (_, servers) -> servers.map { it.name } }

        return grouped
    }
}
