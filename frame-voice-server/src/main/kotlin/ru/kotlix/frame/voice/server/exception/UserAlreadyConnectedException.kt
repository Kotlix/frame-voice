package ru.kotlix.frame.voice.server.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
class UserAlreadyConnectedException(userId: Long) :
    RuntimeException("User $userId is already connected to some session")
