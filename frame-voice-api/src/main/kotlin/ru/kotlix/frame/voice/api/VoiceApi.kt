package ru.kotlix.frame.voice.api

import ru.kotlix.frame.voice.api.dto.ConnectionGuide
import ru.kotlix.frame.voice.api.dto.JoinRequest
import ru.kotlix.frame.voice.api.dto.LeaveRequest

interface VoiceApi {
    fun getUsers(voiceId: Long): List<Long>

    fun getServers(): Map<String, List<String>>

    fun joinChannel(request: JoinRequest): ConnectionGuide

    fun leaveChannel(request: LeaveRequest)
}
