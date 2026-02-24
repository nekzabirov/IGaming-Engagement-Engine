package com.nekgambling.application.usecase.player.spin

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.spin.SpinClosedEvent
import com.nekgambling.application.event.player.spin.SpinOpenedEvent
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.clickhouse.AbstractClickHouseTest
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerSpinRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class SpinUseCaseTest : AbstractClickHouseTest() {

    private val repository = ClickHousePlayerSpinRepository(client)

    private suspend fun seedSpin(spinId: String, playerId: String = "player-1", game: String = "slots-gold"): String {
        PlaceSpinUseCase(repository, currencyAdapter, eventAdapter)(
            PlaceSpinUseCase.Command(
                id = spinId, playerId = playerId, game = game,
                currency = Currency("USD"), realAmount = 500L, bonusAmount = 100L,
            )
        )
        clearMocks(eventAdapter, answers = false)
        coEvery { eventAdapter.publish(any()) } just Runs
        coEvery { currencyAdapter.convertUnitsToSystemUnits(any(), any()) } answers { firstArg() }
        return spinId
    }

    // ========== PlaceSpinUseCase ==========

    @Test
    fun `place spin creates spin and publishes SpinOpenedEvent`() = runTest {
        val useCase = PlaceSpinUseCase(repository, currencyAdapter, eventAdapter)
        val spinId = "spin-${System.nanoTime()}"

        val result = useCase(PlaceSpinUseCase.Command(
            id = spinId,
            playerId = "player-1",
            game = "slots-gold",
            currency = Currency("USD"),
            realAmount = 500L,
            bonusAmount = 100L,
        ))

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        val event = eventSlot.captured
        assertIs<SpinOpenedEvent>(event)
        assertEquals(spinId, event.spin.id)
        assertEquals("player-1", event.spin.playerId)
        assertEquals("slots-gold", event.spin.game)
        assertEquals(Currency("USD"), event.spin.spinCurrency)
        assertEquals(500L, event.spin.placeRealAmount)
        assertEquals(100L, event.spin.placeBonusAmount)
        assertEquals(0L, event.spin.settleRealAmount)
        assertEquals(0L, event.spin.settleBonusAmount)
        assertNull(event.spin.freespinId)

        val saved = repository.findBy("player-1", spinId, "slots-gold")
        assertTrue(saved.isPresent)
        assertEquals(500L, saved.get().placeRealAmount)
        assertEquals(100L, saved.get().placeBonusAmount)
    }

    @Test
    fun `place spin with freespin id`() = runTest {
        val useCase = PlaceSpinUseCase(repository, currencyAdapter, eventAdapter)
        val spinId = "spin-${System.nanoTime()}"

        val result = useCase(PlaceSpinUseCase.Command(
            id = spinId,
            freespinId = "fs-123",
            playerId = "player-1",
            game = "slots-gold",
            currency = Currency("USD"),
            realAmount = 0L,
            bonusAmount = 0L,
        ))

        assertTrue(result.isSuccess)

        val saved = repository.findBy("player-1", spinId, "slots-gold")
        assertTrue(saved.isPresent)
        assertEquals("fs-123", saved.get().freespinId)
    }

    @Test
    fun `place spin converts currency via adapter`() = runTest {
        coEvery { currencyAdapter.convertUnitsToSystemUnits(1000L, Currency("EUR")) } returns 1100L
        coEvery { currencyAdapter.convertUnitsToSystemUnits(200L, Currency("EUR")) } returns 220L

        val useCase = PlaceSpinUseCase(repository, currencyAdapter, eventAdapter)
        val spinId = "spin-${System.nanoTime()}"

        useCase(PlaceSpinUseCase.Command(
            id = spinId, playerId = "player-1", game = "slots-gold",
            currency = Currency("EUR"), realAmount = 1000L, bonusAmount = 200L,
        ))

        val saved = repository.findBy("player-1", spinId, "slots-gold")
        assertTrue(saved.isPresent)
        assertEquals(1100L, saved.get().placeRealAmount)
        assertEquals(220L, saved.get().placeBonusAmount)
    }

    // ========== SettleSpinUseCase ==========

    @Test
    fun `settle spin updates amounts and publishes SpinClosedEvent`() = runTest {
        val spinId = seedSpin("spin-${System.nanoTime()}")

        val useCase = SettleSpinUseCase(repository, currencyAdapter, eventAdapter)
        val result = useCase(SettleSpinUseCase.Command(
            id = spinId, playerId = "player-1", game = "slots-gold",
            realAmount = 2000L, bonusAmount = 300L,
        ))

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        val event = eventSlot.captured
        assertIs<SpinClosedEvent>(event)
        assertEquals(spinId, event.spin.id)
        assertEquals(2000L, event.spin.settleRealAmount)
        assertEquals(300L, event.spin.settleBonusAmount)
        assertEquals(500L, event.spin.placeRealAmount)
        assertEquals(100L, event.spin.placeBonusAmount)

        val saved = repository.findBy("player-1", spinId, "slots-gold")
        assertTrue(saved.isPresent)
        assertEquals(2000L, saved.get().settleRealAmount)
        assertEquals(300L, saved.get().settleBonusAmount)
    }

    @Test
    fun `settle spin fails when spin not found`() = runTest {
        val useCase = SettleSpinUseCase(repository, currencyAdapter, eventAdapter)
        val result = useCase(SettleSpinUseCase.Command(
            id = "nonexistent", playerId = "player-1", game = "slots-gold",
            realAmount = 100L, bonusAmount = 0L,
        ))

        assertTrue(result.isFailure)
        assertIs<IllegalStateException>(result.exceptionOrNull())
        coVerify(exactly = 0) { eventAdapter.publish(any()) }
    }

    @Test
    fun `settle spin with zero win amounts`() = runTest {
        val spinId = seedSpin("spin-${System.nanoTime()}")

        val useCase = SettleSpinUseCase(repository, currencyAdapter, eventAdapter)
        val result = useCase(SettleSpinUseCase.Command(
            id = spinId, playerId = "player-1", game = "slots-gold",
            realAmount = 0L, bonusAmount = 0L,
        ))

        assertTrue(result.isSuccess)

        val saved = repository.findBy("player-1", spinId, "slots-gold")
        assertTrue(saved.isPresent)
        assertEquals(0L, saved.get().settleRealAmount)
        assertEquals(0L, saved.get().settleBonusAmount)
    }

    @Test
    fun `event adapter failure propagates in spin use case`() = runTest {
        coEvery { eventAdapter.publish(any()) } throws RuntimeException("broker down")

        val useCase = PlaceSpinUseCase(repository, currencyAdapter, eventAdapter)
        val result = useCase(PlaceSpinUseCase.Command(
            id = "spin-${System.nanoTime()}", playerId = "p1", game = "g",
            currency = Currency("USD"), realAmount = 100L, bonusAmount = 0L,
        ))

        assertTrue(result.isFailure)
        assertEquals("broker down", result.exceptionOrNull()?.message)
    }
}
