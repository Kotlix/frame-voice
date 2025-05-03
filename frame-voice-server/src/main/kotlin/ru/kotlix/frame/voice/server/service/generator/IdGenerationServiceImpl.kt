package ru.kotlix.frame.voice.server.service.generator

import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.Base64

@Service
class IdGenerationServiceImpl : IdGenerationService {
    private val secureRandom = SecureRandom(LocalDateTime.now().toString().toByteArray())
    private val secretLength = 48

    override fun generateSecret(): String {
        val bytes = ByteArray(secretLength)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    override fun generateChannelId(existing: List<Long>): Long {
        var candidate: Long
        do {
            candidate = secureRandom.nextLong().let { if (it < 0) -it else it }
        } while (existing.contains(candidate))
        return candidate
    }

    override fun generateShadowId(existing: List<Int>): Int {
        var candidate: Int
        do {
            candidate = secureRandom.nextInt(Int.MAX_VALUE)
        } while (existing.contains(candidate))
        return candidate
    }
}
