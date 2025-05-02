package ru.kotlix.frame.voice.server.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotInSessionException(userId: Long) :
    RuntimeException("User $userId is not in sessions!")
