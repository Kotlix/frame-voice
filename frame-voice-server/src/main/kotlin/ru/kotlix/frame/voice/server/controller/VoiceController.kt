package ru.kotlix.frame.voice.server.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.kotlix.frame.voice.api.VoiceApi
import ru.kotlix.frame.voice.api.dto.ConnectionGuide
import ru.kotlix.frame.voice.api.dto.JoinRequest
import ru.kotlix.frame.voice.api.dto.LeaveRequest

@RestController
@RequestMapping("/api/v1/voice")
class VoiceController : VoiceApi {
    override fun joinChannel(request: JoinRequest): ConnectionGuide {
        TODO("Not yet implemented")
    }

    override fun leaveChannel(request: LeaveRequest) {
        TODO("Not yet implemented")
    }
}
