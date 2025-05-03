package ru.kotlix.frame.voice.api

interface ServerApi {
    fun getServers(): Map<String, List<String>>
}
