package ru.kotlix.frame.voice.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import ru.kotlix.frame.voice.server.config.props.Anchor

@ConfigurationPropertiesScan(basePackageClasses = [Anchor::class])
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
