package ru.kotlix.frame.voice.server.service

import feign.FeignException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.kotlix.frame.state.client.UserStateClient
import ru.kotlix.frame.voice.api.dto.ConnectionGuide
import ru.kotlix.frame.voice.server.exception.ServerNotFoundException
import ru.kotlix.frame.voice.server.exception.UserAlreadyConnectedException
import ru.kotlix.frame.voice.server.exception.UserNotInSessionException
import ru.kotlix.frame.voice.server.exception.UserNotOnlineException
import ru.kotlix.frame.voice.server.exception.VoiceNotFoundException
import ru.kotlix.frame.voice.server.repository.AttendantRepository
import ru.kotlix.frame.voice.server.repository.ServerRepository
import ru.kotlix.frame.voice.server.repository.VoiceSessionRepository
import ru.kotlix.frame.voice.server.repository.dto.AttendantEntity
import ru.kotlix.frame.voice.server.repository.dto.VoiceSessionEntity
import ru.kotlix.frame.voice.server.service.generator.IdGenerationService
import java.time.OffsetDateTime

@Service
class VoiceServiceImpl(
    private val voiceSessionRepository: VoiceSessionRepository,
    private val serverRepository: ServerRepository,
    private val attendantRepository: AttendantRepository,
    private val userStateClient: UserStateClient,
    private val generatorService: IdGenerationService,
) : VoiceService {
    @Transactional
    override fun getUsers(voiceId: Long): List<Long> {
        val voice =
            voiceSessionRepository.findByVoiceId(voiceId)
                ?: throw VoiceNotFoundException(voiceId)

        return attendantRepository.findAllByVoiceSessionId(voice.voiceId).map { it.userId }
    }

    @Transactional
    override fun joinChannel(
        userId: Long,
        voiceId: Long,
        serverName: String,
        serverRegion: String,
    ): ConnectionGuide {
        try {
            val userOnline = userStateClient.getUserStatus(userId).online
            if (!userOnline) throw UserNotOnlineException(userId)
        } catch (ex: FeignException.NotFound) {
            throw RuntimeException("Failed to fetch user status for user $userId", ex)
        }

        attendantRepository.findByUserId(userId)?.let {
            throw UserAlreadyConnectedException(userId)
        }

        val server =
            serverRepository.findByNameAndRegion(serverName, serverRegion)
                ?: throw ServerNotFoundException(serverName, serverRegion)

        val sessionEntity = voiceSessionRepository.findByVoiceId(voiceId)

        val allChannelIds =
            voiceSessionRepository
                .findAllByServerId(server.id!!)
                .map { it.channelId }

        val savedSession =
            sessionEntity ?: voiceSessionRepository.save(
                VoiceSessionEntity(
                    createdAt = OffsetDateTime.now(),
                    serverId = server.id,
                    channelId = generatorService.generateChannelId(allChannelIds),
                    voiceId = voiceId,
                    secret = generatorService.generateSecret(),
                ),
            )

        val allShadowsIds =
            attendantRepository
                .findAllByVoiceSessionId(savedSession.id!!)
                .map { it.shadowId }

        val savedAttendant =
            attendantRepository.save(
                AttendantEntity(
                    joinedAt = OffsetDateTime.now(),
                    userId = userId,
                    voiceSessionId = savedSession.id,
                    shadowId = generatorService.generateShadowId(allShadowsIds),
                ),
            )

        return ConnectionGuide(
            hostAddress = server.hostAddress,
            channelId = savedSession.channelId,
            shadowId = savedAttendant.shadowId,
        )
    }

    @Transactional
    override fun leaveChannel(userId: Long) {
        val existingAttendant =
            attendantRepository.findByUserId(userId) ?: throw UserNotInSessionException(userId)

        attendantRepository.removeByUserId(userId)
        if (attendantRepository.findAllByVoiceSessionId(existingAttendant.voiceSessionId).isEmpty()) {
            voiceSessionRepository.removeById(existingAttendant.voiceSessionId)
        }
    }
}
