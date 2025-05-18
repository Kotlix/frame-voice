package ru.kotlix.frame.voice.server.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ServerNotFoundException(name: String, region: String) :
    RuntimeException("Server $name $region is not found")
