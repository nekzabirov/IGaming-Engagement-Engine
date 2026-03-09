package com.nekgambling.infrastructure.database.clickhouse.config

data class ClickHouseConfig(
    val url: String,
    val username: String = "default",
    val password: String = "",
)
