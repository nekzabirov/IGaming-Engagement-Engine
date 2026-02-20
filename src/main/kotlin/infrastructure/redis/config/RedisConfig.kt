package com.nekgambling.infrastructure.redis.config

data class RedisConfig(
    val host: String,
    val port: Int,
    val password: String? = null,
)
