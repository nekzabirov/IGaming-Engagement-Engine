package com.nekgambling.infrastructure.external.rabbitmq.config

data class RabbitMQConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val exchange: String,
    val exchangeType: String = "topic",
)
