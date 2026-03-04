import com.google.protobuf.gradle.id

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobuf)
    `maven-publish`
    application
}

application {
    mainClass.set("com.nekgambling.ApplicationKt")
}

group = "com.nekgambling"
version = "1.0.0"

val grpcClientVersion: String by project

val grpcVersion = "1.68.2"
val grpcKotlinVersion = "1.4.1"
val protobufVersion = "4.29.2"

kotlin {
    jvmToolchain(21)
}

dependencies {
    // Ktor Server
    implementation(libs.bundles.ktor.server)
    implementation("io.ktor:ktor-server-config-yaml:${libs.versions.ktor.get()}")
    implementation(libs.logback)

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // DateTime
    implementation(libs.kotlinx.datetime)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // gRPC
    implementation(libs.bundles.grpc)
    implementation(libs.protobuf.kotlin)

    // Koin
    implementation(libs.bundles.koin)

    // Database
    implementation(libs.bundles.exposed)
    implementation(libs.postgresql)
    implementation(libs.hikaricp)

    // Redis
    implementation(libs.jedis)

    // ClickHouse
    implementation(libs.clickhouse.jdbc) {
        artifact {
            classifier = "all"
        }
    }

    // RabbitMQ
    implementation("com.rabbitmq:amqp-client:5.22.0")

    // Testing
    testImplementation(libs.bundles.testing)
    testImplementation(libs.clickhouse.jdbc) {
        artifact {
            classifier = "all"
        }
    }
}