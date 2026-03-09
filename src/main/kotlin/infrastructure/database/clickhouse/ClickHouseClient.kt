package com.nekgambling.infrastructure.database.clickhouse

import com.nekgambling.infrastructure.database.clickhouse.config.ClickHouseConfig
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.use

class ClickHouseClient(private val config: ClickHouseConfig) {

    private fun connection(): Connection {
        val props = Properties().apply {
            setProperty("user", config.username)
            setProperty("password", config.password)
        }
        return DriverManager.getConnection(config.url, props)
    }

    suspend fun execute(sql: String, params: List<Any?> = emptyList()) {
        withContext(Dispatchers.IO) {
            connection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    params.forEachIndexed { index, value ->
                        stmt.setObject(index + 1, value)
                    }
                    stmt.execute()
                }
            }
        }
    }

    suspend fun <T> query(sql: String, params: List<Any?> = emptyList(), mapper: (ResultSet) -> T): List<T> {
        return withContext(Dispatchers.IO) {
            connection().use { conn ->
                conn.prepareStatement(sql).use { stmt ->
                    params.forEachIndexed { index, value ->
                        stmt.setObject(index + 1, value)
                    }
                    stmt.executeQuery().use { rs ->
                        val results = mutableListOf<T>()
                        while (rs.next()) {
                            results.add(mapper(rs))
                        }
                        results
                    }
                }
            }
        }
    }

    suspend fun <T> queryOne(sql: String, params: List<Any?> = emptyList(), mapper: (ResultSet) -> T): T? {
        return query(sql, params, mapper).firstOrNull()
    }

    fun initTables() {
        val sqlFiles = listOf("clickhouse/init.sql", "clickhouse/meterized_view.sql")

        connection().use { conn ->
            sqlFiles.forEach { file ->
                val sql = this::class.java.classLoader
                    .getResourceAsStream(file)
                    ?.bufferedReader()
                    ?.readText()
                    ?: error("$file not found on classpath")

                sql.split(";")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .forEach { statement ->
                        conn.createStatement().use { stmt ->
                            stmt.execute(statement)
                        }
                    }
            }
        }
    }
}
