package com.nekgambling.infrastructure.clickhouse.reader

import com.nekgambling.application.usecase.player.spin.PlaceSpinUseCase
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.vo.Period
import com.nekgambling.infrastructure.clickhouse.AbstractClickHouseTest
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerSpinRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.*
import kotlin.time.Duration.Companion.days

class PlayerSpinTotalReaderTest : AbstractClickHouseTest() {

    private val spinRepository = ClickHousePlayerSpinRepository(client)
    private val reader = ClickHousePlayerSpinTotalReader(client)

    private fun todayPeriod(): Period = Pair(
        Clock.System.now().minus(1.days),
        Clock.System.now().plus(1.days),
    )

    private suspend fun placeSpin(
        spinId: String,
        playerId: String = "player-1",
        game: String = "slots-gold",
        realAmount: Long = 500L,
        bonusAmount: Long = 100L,
        freespinId: String? = null,
    ) {
        val result = PlaceSpinUseCase(spinRepository, currencyAdapter, eventAdapter)(
            PlaceSpinUseCase.Command(
                id = spinId,
                playerId = playerId,
                game = game,
                currency = Currency("USD"),
                realAmount = realAmount,
                bonusAmount = bonusAmount,
                freespinId = freespinId,
            )
        )
        assertTrue(result.isSuccess, "PlaceSpinUseCase failed: ${result.exceptionOrNull()?.message}")
    }

    @Test
    fun `returns zeros when no data exists`() = runTest {
        val result = reader.read("player-1", todayPeriod())
        assertEquals(0L, result.placeAmount)
        assertEquals(0L, result.settleAmount)
        assertEquals(0L, result.realPlaceAmount)
        assertEquals(0L, result.realSettleAmount)
    }

    @Test
    fun `returns correct totals after placing spins`() = runTest {
        placeSpin("spin-1", realAmount = 500L, bonusAmount = 100L)
        placeSpin("spin-2", realAmount = 300L, bonusAmount = 50L)

        val result = reader.read("player-1", todayPeriod())
        // placeAmount = (500+100) + (300+50) = 950
        assertEquals(950L, result.placeAmount)
        assertEquals(0L, result.settleAmount)
        // realPlaceAmount = 500 + 300 = 800 (no freespins)
        assertEquals(800L, result.realPlaceAmount)
        assertEquals(0L, result.realSettleAmount)
    }

    @Test
    fun `freespin spins do not count toward real amounts`() = runTest {
        placeSpin("spin-1", realAmount = 500L, bonusAmount = 100L)
        placeSpin("spin-2", realAmount = 200L, bonusAmount = 0L, freespinId = "fs-1")

        val result = reader.read("player-1", todayPeriod())
        // placeAmount = (500+100) + (200+0) = 800
        assertEquals(800L, result.placeAmount)
        // realPlaceAmount = 500 only (freespin spin excluded)
        assertEquals(500L, result.realPlaceAmount)
    }

    @Test
    fun `filters by player_id`() = runTest {
        placeSpin("spin-1", playerId = "player-1", realAmount = 500L, bonusAmount = 0L)
        placeSpin("spin-2", playerId = "player-2", realAmount = 300L, bonusAmount = 0L)

        val result1 = reader.read("player-1", todayPeriod())
        assertEquals(500L, result1.placeAmount)
        assertEquals(500L, result1.realPlaceAmount)

        val result2 = reader.read("player-2", todayPeriod())
        assertEquals(300L, result2.placeAmount)
        assertEquals(300L, result2.realPlaceAmount)
    }

    @Test
    fun `returns zeros for period outside data range`() = runTest {
        placeSpin("spin-1", realAmount = 500L, bonusAmount = 100L)

        val farFuture: Period = Pair(
            Clock.System.now().plus(30.days),
            Clock.System.now().plus(60.days),
        )
        val result = reader.read("player-1", farFuture)
        assertEquals(0L, result.placeAmount)
        assertEquals(0L, result.settleAmount)
        assertEquals(0L, result.realPlaceAmount)
        assertEquals(0L, result.realSettleAmount)
    }
}
