package ru.kotlix.frame.voice.server.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.kotlix.frame.voice.api.VoiceApi
import ru.kotlix.frame.voice.api.dto.ConnectionGuide
import ru.kotlix.frame.voice.api.dto.JoinRequest
import ru.kotlix.frame.voice.api.dto.LeaveRequest
import ru.kotlix.frame.voice.server.service.VoiceService

@RestController
@RequestMapping("/api/v1/voice")
class VoiceController(
    val voiceService: VoiceService,
) : VoiceApi {
    @PostMapping("/join")
    override fun joinChannel(
        @RequestBody request: JoinRequest,
    ): ConnectionGuide {
        return voiceService.joinChannel(request)
    }

    @PostMapping("/leave")
    override fun leaveChannel(
        @RequestBody request: LeaveRequest,
    ) {
        voiceService.leaveChannel(request)
    }
}
