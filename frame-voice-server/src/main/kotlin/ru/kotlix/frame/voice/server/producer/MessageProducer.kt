package ru.kotlix.frame.voice.server.producer

import ru.kotlix.frame.router.api.kafka.VoiceNotification

interface MessageProducer {
    fun send(message: VoiceNotification)
}
