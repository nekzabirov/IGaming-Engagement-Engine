package com.nekgambling.infrastructure.clickhouse

import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.infrastructure.clickhouse.config.ClickHouseConfig
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import java.sql.DriverManager
import kotlin.test.BeforeTest

abstract class AbstractClickHouseTest {

    protected val client: ClickHouseClient = ClickHouseClient(CONFIG)
    protected val eventAdapter: IEventAdapter = mockk()
    protected val currencyAdapter: ICurrencyAdapter = mockk()

    @BeforeTest
    fun baseSetUp() {
        truncateAll()
        client.initTables()

        coEvery { eventAdapter.publish(any()) } just Runs
        coEvery { currencyAdapter.convertUnitsToSystemUnits(any(), any()) } answers { firstArg() }
    }

    companion object {
        val CONFIG = ClickHouseConfig(
            url = System.getenv("CLICKHOUSE_URL") ?: "jdbc:clickhouse://localhost:8123/default",
            username = System.getenv("CLICKHOUSE_USERNAME") ?: "default",
            password = System.getenv("CLICKHOUSE_PASSWORD") ?: "",
        )

        private fun truncateAll() {
            DriverManager.getConnection(CONFIG.url, CONFIG.username, CONFIG.password).use { conn ->
                conn.createStatement().use { stmt ->
                    for (table in listOf(
                        ClickHouseTable.PLAYER_DETAILS,
                        ClickHouseTable.PLAYER_BONUS,
                        ClickHouseTable.PLAYER_SPIN,
                        ClickHouseTable.PLAYER_FREESPIN,
                        ClickHouseTable.PLAYER_INVOICE,
                        ClickHouseTable.PLAYER_INVOICE_TOTAL,
                        ClickHouseTable.PLAYER_TOTAL_SPIN_INFO,
                    )) {
                        stmt.execute("TRUNCATE TABLE IF EXISTS $table")
                    }
                }
            }
        }
    }
}
