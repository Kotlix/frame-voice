package ru.kotlix.frame.voice.server.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class VoiceNotFoundException(voiceId: Long) :
    RuntimeException("Voice $voiceId is not found")
