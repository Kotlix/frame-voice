package ru.kotlix.frame.voice.server.service

import ru.kotlix.frame.voice.api.dto.ConnectionGuide

interface VoiceService {
    fun getUsers(voiceId: Long): List<Long>

    fun joinChannel(
        userId: Long,
        voiceId: Long,
        serverName: String,
        serverRegion: String,
    ): ConnectionGuide

    fun leaveChannel(userId: Long)
}
