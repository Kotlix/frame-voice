package ru.kotlix.frame.voice.server.producer

import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import ru.kotlix.frame.router.api.kafka.VoiceNotification

@Component
class KafkaMessageProducer(
    @Value("\${voice.kafka.producer.topic}")
    private val topicName: String,
    private val kafkaTemplate: KafkaTemplate<Void, VoiceNotification>,
) : MessageProducer {
    override fun send(message: VoiceNotification) {
        kafkaTemplate.send(topicName, message)
    }
}
