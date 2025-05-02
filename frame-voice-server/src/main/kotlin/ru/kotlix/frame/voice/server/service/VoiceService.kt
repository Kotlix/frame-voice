package ru.kotlix.frame.voice.server.service

import ru.kotlix.frame.voice.api.dto.ConnectionGuide
import ru.kotlix.frame.voice.api.dto.JoinRequest
import ru.kotlix.frame.voice.api.dto.LeaveRequest

interface VoiceService {
    fun joinChannel(request: JoinRequest): ConnectionGuide

    fun leaveChannel(request: LeaveRequest)
}
