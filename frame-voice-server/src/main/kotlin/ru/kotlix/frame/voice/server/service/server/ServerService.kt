package ru.kotlix.frame.voice.server.service.server

interface ServerService {
    fun getServersGroupedByRegion(): Map<String, List<String>>
}
