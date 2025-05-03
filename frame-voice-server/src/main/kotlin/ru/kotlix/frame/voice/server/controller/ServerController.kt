package ru.kotlix.frame.voice.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.kotlix.frame.voice.api.ServerApi
import ru.kotlix.frame.voice.server.service.server.ServerService

@RestController
@RequestMapping("/api/v1/server")
class ServerController(
    val serverService: ServerService,
) : ServerApi {
    @GetMapping("/all")
    override fun getServers(): Map<String, List<String>> {
        return serverService.getServersGroupedByRegion()
    }
}
