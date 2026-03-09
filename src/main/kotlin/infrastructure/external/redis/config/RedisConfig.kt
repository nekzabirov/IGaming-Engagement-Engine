package com.nekgambling.infrastructure.external.redis.config

data class RedisConfig(
    val host: String,
    val port: Int,
    val password: String? = null,
)
