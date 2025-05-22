package ru.kotlix.frame.voice.server.service

import feign.FeignException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.kotlix.frame.router.api.kafka.Attendant
import ru.kotlix.frame.router.api.kafka.ConnectionInfo
import ru.kotlix.frame.router.api.kafka.UpdateInfo
import ru.kotlix.frame.router.api.kafka.VoiceInfo
import ru.kotlix.frame.router.api.kafka.VoiceNotification
import ru.kotlix.frame.state.client.UserStateClient
import ru.kotlix.frame.voice.api.dto.ConnectionGuide
import ru.kotlix.frame.voice.server.exception.ServerNotFoundException
import ru.kotlix.frame.voice.server.exception.UserAlreadyConnectedException
import ru.kotlix.frame.voice.server.exception.UserNotInSessionException
import ru.kotlix.frame.voice.server.exception.UserNotOnlineException
import ru.kotlix.frame.voice.server.exception.VoiceNotFoundException
import ru.kotlix.frame.voice.server.producer.MessageProducer
import ru.kotlix.frame.voice.server.repository.AttendantRepository
import ru.kotlix.frame.voice.server.repository.ServerRepository
import ru.kotlix.frame.voice.server.repository.VoiceSessionRepository
import ru.kotlix.frame.voice.server.repository.dto.AttendantEntity
import ru.kotlix.frame.voice.server.repository.dto.ServerEntity
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
    private val messageProducer: MessageProducer,
) : VoiceService {
    @Transactional
    override fun getUsers(voiceId: Long): List<Long> {
        val voice =
            voiceSessionRepository.findByVoiceId(voiceId)
                ?: throw VoiceNotFoundException(voiceId)

        return attendantRepository.findAllByVoiceSessionId(voice.id!!).map { it.userId }
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

        val allChannelIds =
            voiceSessionRepository
                .findAllByServerId(server.id!!)
                .map { it.channelId }

        val sessionEntity =
            voiceSessionRepository.findByVoiceId(voiceId)
                ?: voiceSessionRepository.save(
                    VoiceSessionEntity(
                        createdAt = OffsetDateTime.now(),
                        serverId = server.id,
                        channelId = generatorService.generateChannelId(allChannelIds),
                        voiceId = voiceId,
                        secret = generatorService.generateSecret(),
                    ),
                )

        val allAttendants = attendantRepository.findAllByVoiceSessionId(sessionEntity.id!!)

        val savedAttendant =
            attendantRepository.save(
                AttendantEntity(
                    joinedAt = OffsetDateTime.now(),
                    userId = userId,
                    voiceSessionId = sessionEntity.id,
                    shadowId = generatorService.generateShadowId(allAttendants.map { it.shadowId }),
                ),
            )

        messageProducer.send(
            buildVoiceNotify(
                serverEntity = server,
                sessionEntity = sessionEntity,
                attendants = allAttendants,
                changed = savedAttendant,
                join = true,
            ),
        )

        return ConnectionGuide(
            hostAddress = server.hostAddress,
            secret = sessionEntity.secret,
            channelId = sessionEntity.channelId,
            shadowId = savedAttendant.shadowId,
        )
    }

    private fun buildVoiceNotify(
        serverEntity: ServerEntity,
        sessionEntity: VoiceSessionEntity,
        attendants: List<AttendantEntity>,
        changed: AttendantEntity,
        join: Boolean,
    ) = VoiceNotification()
        .withConnectionInfo(
            ConnectionInfo()
                .withVoiceRegion(serverEntity.region)
                .withVoiceName(serverEntity.name)
                .withChannelId(sessionEntity.channelId),
        )
        .withVoiceInfo(
            VoiceInfo()
                .withVoiceId(sessionEntity.voiceId)
                .withParty(
                    attendants.map { att ->
                        Attendant()
                            .withUserId(att.userId)
                            .withShadowId(att.shadowId)
                    },
                ),
        )
        .withUpdateInfo(
            UpdateInfo()
                .withAction(
                    if (join) {
                        UpdateInfo.Action.JOINED
                    } else {
                        UpdateInfo.Action.LEFT
                    },
                )
                .withAttendant(
                    Attendant()
                        .withUserId(changed.userId)
                        .withShadowId(changed.shadowId),
                ),
        )

    @Transactional
    override fun leaveChannel(userId: Long) {
        val removingAttendant =
            attendantRepository.findByUserId(userId) ?: throw UserNotInSessionException(userId)
        val sessionEntity =
            voiceSessionRepository.findById(removingAttendant.voiceSessionId)
                ?: throw RuntimeException(
                    "Attendant id=${removingAttendant.id} exists but its " +
                        "session id=${removingAttendant.voiceSessionId}",
                )
        val serverEntity =
            serverRepository.findById(sessionEntity.serverId)
                ?: throw RuntimeException(
                    "Session id=${sessionEntity.id} exists but its " +
                        "server id=${sessionEntity.serverId}",
                )
        attendantRepository.removeByUserId(removingAttendant.userId)
        val allAttendants = attendantRepository.findAllByVoiceSessionId(removingAttendant.voiceSessionId)
        if (allAttendants.isEmpty()) {
            voiceSessionRepository.removeById(sessionEntity.id!!)
        }

        messageProducer.send(
            buildVoiceNotify(
                serverEntity = serverEntity,
                sessionEntity = sessionEntity,
                attendants = allAttendants,
                changed = removingAttendant,
                join = false,
            ),
        )
    }
}
