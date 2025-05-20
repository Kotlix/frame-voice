package ru.kotlix.frame.voice.server.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.serialization.VoidSerializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import ru.kotlix.frame.router.api.kafka.VoiceNotification
import ru.kotlix.frame.voice.server.config.props.KafkaAuthProperties

@EnableKafka
@Configuration
class KafkaConfig {
    @Bean
    fun producerFactory(
        kafkaProperties: KafkaProperties,
        kafkaAuth: KafkaAuthProperties,
        objectMapper: ObjectMapper,
    ): ProducerFactory<Void, VoiceNotification> =
        DefaultKafkaProducerFactory<Void, VoiceNotification>(
            HashMap<String, Any>()
                .withEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaProperties.bootstrapServers)
                .withEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to VoidSerializer::class.java)
                .withEntry(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to "SASL_PLAINTEXT")
                .withEntry(SaslConfigs.SASL_MECHANISM to "PLAIN")
                .withEntry(SaslConfigs.SASL_JAAS_CONFIG to saslJaasConfig(kafkaAuth)),
        ).apply {
            valueSerializer = JsonSerializer(objectMapper)
        }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<Void, VoiceNotification>): KafkaTemplate<Void, VoiceNotification> =
        KafkaTemplate(producerFactory)

    private fun saslJaasConfig(kafkaAuth: KafkaAuthProperties) =
        "org.apache.kafka.common.security.plain.PlainLoginModule required " +
            "username=\"${kafkaAuth.username}\" " +
            "password=\"${kafkaAuth.password}\" " +
            "user_${kafkaAuth.username}=\"${kafkaAuth.password}\";"

    private fun <K, V> MutableMap<K, V>.withEntry(entry: Pair<K, V>) =
        this.apply {
            this[entry.first] = entry.second
        }
}
