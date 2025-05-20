package ru.kotlix.frame.voice.server.config.props

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "voice.kafka.authentication")
data class KafkaAuthProperties(
    var username: String,
    var password: String,
)
