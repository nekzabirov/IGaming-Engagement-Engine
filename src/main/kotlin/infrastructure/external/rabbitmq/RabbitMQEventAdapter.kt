package com.nekgambling.infrastructure.external.rabbitmq

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.infrastructure.external.rabbitmq.config.RabbitMQConfig
import com.nekgambling.infrastructure.external.rabbitmq.mapper.routingKey
import com.nekgambling.infrastructure.external.rabbitmq.mapper.toJson
import com.rabbitmq.client.BuiltinExchangeType
import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RabbitMQEventAdapter(private val config: RabbitMQConfig) : IEventAdapter {

    private val channel: Channel by lazy {
        val factory = ConnectionFactory().apply {
            host = config.host
            port = config.port
            username = config.username
            password = config.password
        }

        val connection = factory.newConnection()
        val ch = connection.createChannel()

        ch.exchangeDeclare(
            config.exchange,
            BuiltinExchangeType.valueOf(config.exchangeType.uppercase()),
            true,
        )

        ch
    }

    override suspend fun publish(event: IEventAdapter.AppEvent) {
        val routingKey = event.routingKey()
        val body = event.toJson().toByteArray(Charsets.UTF_8)

        withContext(Dispatchers.IO) {
            channel.basicPublish(config.exchange, routingKey, null, body)
        }
    }
}
