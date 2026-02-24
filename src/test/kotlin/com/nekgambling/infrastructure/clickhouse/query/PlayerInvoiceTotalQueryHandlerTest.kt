package com.nekgambling.infrastructure.clickhouse.query

import com.nekgambling.application.query.player.GetPlayerInvoiceTotalQuery
import com.nekgambling.application.usecase.player.invoice.CreateInvoiceUseCase
import com.nekgambling.domain.player.model.PlayerInvoice
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.vo.Period
import com.nekgambling.infrastructure.clickhouse.AbstractClickHouseTest
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerInvoiceRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.*
import kotlin.time.Duration.Companion.days

class PlayerInvoiceTotalQueryHandlerTest : AbstractClickHouseTest() {

    private val invoiceRepository = ClickHousePlayerInvoiceRepository(client)
    private val handler = ClickHousePlayerInvoiceTotalQueryHandler(client)

    private fun todayPeriod(): Period = Pair(
        Clock.System.now().minus(1.days),
        Clock.System.now().plus(1.days),
    )

    private suspend fun createInvoice(
        invoiceId: String,
        playerId: String = "player-1",
        type: PlayerInvoice.Type = PlayerInvoice.Type.DEPOSIT,
        amount: Long = 10000L,
    ) {
        CreateInvoiceUseCase(invoiceRepository, eventAdapter, currencyAdapter)(
            playerId,
            CreateInvoiceUseCase.Transaction(
                id = invoiceId,
                type = type,
                currency = Currency("USD"),
                amount = amount,
                createdAt = Clock.System.now(),
            )
        )
    }

    @Test
    fun `returns zeros when no data exists`() = runTest {
        val result = handler.handle(GetPlayerInvoiceTotalQuery("player-1", todayPeriod()))
        assertEquals(0L, result.depositAmount)
        assertEquals(0L, result.withdrawAmount)
        assertEquals(0, result.depositCount)
        assertEquals(0, result.withdrawCount)
        assertEquals(0L, result.taxAmount)
        assertEquals(0L, result.feesAmount)
    }

    @Test
    fun `returns correct deposit totals`() = runTest {
        createInvoice("inv-1", type = PlayerInvoice.Type.DEPOSIT, amount = 10000L)
        createInvoice("inv-2", type = PlayerInvoice.Type.DEPOSIT, amount = 5000L)

        val result = handler.handle(GetPlayerInvoiceTotalQuery("player-1", todayPeriod()))
        assertEquals(15000L, result.depositAmount)
        assertEquals(2, result.depositCount)
        assertEquals(0L, result.withdrawAmount)
        assertEquals(0, result.withdrawCount)
    }

    @Test
    fun `returns correct withdrawal totals`() = runTest {
        createInvoice("inv-1", type = PlayerInvoice.Type.PAYOUT, amount = 3000L)

        val result = handler.handle(GetPlayerInvoiceTotalQuery("player-1", todayPeriod()))
        assertEquals(0L, result.depositAmount)
        assertEquals(0, result.depositCount)
        assertEquals(3000L, result.withdrawAmount)
        assertEquals(1, result.withdrawCount)
    }

    @Test
    fun `returns combined deposit and withdrawal totals`() = runTest {
        createInvoice("inv-1", type = PlayerInvoice.Type.DEPOSIT, amount = 10000L)
        createInvoice("inv-2", type = PlayerInvoice.Type.PAYOUT, amount = 3000L)
        createInvoice("inv-3", type = PlayerInvoice.Type.DEPOSIT, amount = 5000L)

        val result = handler.handle(GetPlayerInvoiceTotalQuery("player-1", todayPeriod()))
        assertEquals(15000L, result.depositAmount)
        assertEquals(2, result.depositCount)
        assertEquals(3000L, result.withdrawAmount)
        assertEquals(1, result.withdrawCount)
    }

    @Test
    fun `filters by player_id`() = runTest {
        createInvoice("inv-1", playerId = "player-1", type = PlayerInvoice.Type.DEPOSIT, amount = 10000L)
        createInvoice("inv-2", playerId = "player-2", type = PlayerInvoice.Type.DEPOSIT, amount = 5000L)

        val result1 = handler.handle(GetPlayerInvoiceTotalQuery("player-1", todayPeriod()))
        assertEquals(10000L, result1.depositAmount)
        assertEquals(1, result1.depositCount)

        val result2 = handler.handle(GetPlayerInvoiceTotalQuery("player-2", todayPeriod()))
        assertEquals(5000L, result2.depositAmount)
        assertEquals(1, result2.depositCount)
    }

    @Test
    fun `returns zeros for period outside data range`() = runTest {
        createInvoice("inv-1", type = PlayerInvoice.Type.DEPOSIT, amount = 10000L)

        val farFuture: Period = Pair(
            Clock.System.now().plus(30.days),
            Clock.System.now().plus(60.days),
        )
        val result = handler.handle(GetPlayerInvoiceTotalQuery("player-1", farFuture))
        assertEquals(0L, result.depositAmount)
        assertEquals(0, result.depositCount)
        assertEquals(0L, result.withdrawAmount)
        assertEquals(0, result.withdrawCount)
    }
}
