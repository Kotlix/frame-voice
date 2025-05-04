package ru.kotlix.frame.voice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.kotlix.frame.voice.api.VoiceApi
import ru.kotlix.frame.voice.api.dto.ConnectionGuide
import ru.kotlix.frame.voice.api.dto.JoinRequest
import ru.kotlix.frame.voice.api.dto.LeaveRequest

@FeignClient(name = "frame-voice-client", path = "/api/v1")
interface VoiceClient : VoiceApi {
    @PostMapping("/voice/join")
    override fun joinChannel(
        @RequestBody request: JoinRequest,
    ): ConnectionGuide

    @PostMapping("/voice/leave")
    override fun leaveChannel(
        @RequestBody request: LeaveRequest,
    )

    @GetMapping("/server/all")
    override fun getServers(): Map<String, List<String>>
}
