package com.nekgambling.infrastructure.exposed

import com.nekgambling.infrastructure.exposed.table.ConditionResultsTable
import com.nekgambling.infrastructure.exposed.table.ConditionsTable
import com.nekgambling.infrastructure.exposed.table.JourneyNodesTable
import com.nekgambling.infrastructure.exposed.table.JourneysTable
import com.nekgambling.infrastructure.exposed.table.SegmentResultsTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object ExposedDatabaseFactory {

    fun init(config: ExposedConfig): Database {
        val dataSource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = config.url
            driverClassName = "org.postgresql.Driver"
            username = config.username
            password = config.password
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })

        return Database.connect(dataSource).also { db ->
            transaction(db) {
                SchemaUtils.create(
                    ConditionsTable,
                    ConditionResultsTable,
                    SegmentResultsTable,
                    JourneyNodesTable,
                    JourneysTable,
                )
            }
        }
    }
}
