package ru.kotlix.frame.voice.server.service

import feign.FeignException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.kotlix.frame.state.client.UserStateClient
import ru.kotlix.frame.voice.api.dto.ConnectionGuide
import ru.kotlix.frame.voice.api.dto.JoinRequest
import ru.kotlix.frame.voice.api.dto.LeaveRequest
import ru.kotlix.frame.voice.server.exception.ServerNotFoundException
import ru.kotlix.frame.voice.server.exception.UserAlreadyConnectedException
import ru.kotlix.frame.voice.server.exception.UserNotInSessionException
import ru.kotlix.frame.voice.server.exception.UserNotOnlineException
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
    override fun joinChannel(request: JoinRequest): ConnectionGuide {
        try {
            val userOnline = userStateClient.getUserStatus(request.userId).online
            if (!userOnline) throw UserNotOnlineException(request.userId)
        } catch (ex: FeignException.NotFound) {
            throw RuntimeException("Failed to fetch user status for user ${request.userId}", ex)
        }

        attendantRepository.findByUserId(request.userId)?.let {
            throw UserAlreadyConnectedException(request.userId)
        }

        val server =
            serverRepository.findByNameAndRegion(request.serverName, request.serverRegion)
                ?: throw ServerNotFoundException(request.serverName, request.serverRegion)

        val sessionEntity = voiceSessionRepository.findByVoiceId(request.voiceId)

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
                    voiceId = request.voiceId,
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
                    userId = request.userId,
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
    override fun leaveChannel(request: LeaveRequest) {
        val existingAttendant =
            attendantRepository.findByUserId(request.userId) ?: throw UserNotInSessionException(request.userId)

        attendantRepository.removeByUserId(request.userId)
        if (attendantRepository.findAllByVoiceSessionId(existingAttendant.voiceSessionId).isEmpty()) {
            voiceSessionRepository.removeById(existingAttendant.voiceSessionId)
        }
    }
}
