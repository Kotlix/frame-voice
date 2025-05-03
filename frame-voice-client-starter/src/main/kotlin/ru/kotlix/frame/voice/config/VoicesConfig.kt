package ru.kotlix.frame.voice.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackageClasses = [ru.kotlix.frame.voice.client.VoiceClient::class])
@ConditionalOnMissingBean(ru.kotlix.frame.voice.client.VoiceClient::class)
class VoicesConfig
