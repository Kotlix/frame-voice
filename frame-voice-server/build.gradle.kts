import org.springframework.boot.gradle.tasks.bundling.BootJar

val frameStateVersion: String by project

dependencies {
    api(project(":frame-voice-api"))

    runtimeOnly("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    implementation("ru.kotlix:frame-state-client-starter:1.0.0-SNAPSHOT")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.liquibase:liquibase-core")

    implementation("org.springframework:spring-context-support")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.liquibase:liquibase-core")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")

    runtimeOnly("org.postgresql:postgresql")
    implementation("ru.kotlix:frame-state-client-starter:$frameStateVersion")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
