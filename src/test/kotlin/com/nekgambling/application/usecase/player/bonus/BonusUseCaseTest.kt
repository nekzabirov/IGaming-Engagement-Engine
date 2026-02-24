package com.nekgambling.application.usecase.player.bonus

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.bonus.BonusIssuedEvent
import com.nekgambling.application.event.player.bonus.BonusLostEvent
import com.nekgambling.application.event.player.bonus.BonusStartWageringEvent
import com.nekgambling.application.event.player.bonus.BonusWageredEvent
import com.nekgambling.domain.player.model.PlayerBonus
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.clickhouse.AbstractClickHouseTest
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerBonusRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class BonusUseCaseTest : AbstractClickHouseTest() {

    private val repository = ClickHousePlayerBonusRepository(client)

    // ========== IssueBonusUseCase ==========

    @Test
    fun `issue bonus creates bonus and publishes BonusIssuedEvent`() = runTest {
        val useCase = IssueBonusUseCase(repository, eventAdapter, currencyAdapter)
        val command = IssueBonusUseCase.Command(
            id = "bonus-${System.nanoTime()}",
            identity = "welcome-bonus",
            playerId = "player-1",
            amount = 5000L,
            currency = Currency("USD"),
        )

        val result = useCase(command)

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        val event = eventSlot.captured
        assertIs<BonusIssuedEvent>(event)
        assertEquals(command.id, event.bonus.id)
        assertEquals("welcome-bonus", event.bonus.identity)
        assertEquals("player-1", event.bonus.playerId)
        assertEquals(PlayerBonus.Status.ISSUE, event.bonus.status)
        assertEquals(5000L, event.bonus.amount)
        assertEquals(0L, event.bonus.payoutAmount)

        val saved = repository.findById(command.id)
        assertTrue(saved.isPresent)
        assertEquals(PlayerBonus.Status.ISSUE, saved.get().status)
        assertEquals(5000L, saved.get().amount)
    }

    @Test
    fun `issue bonus converts currency via adapter`() = runTest {
        coEvery { currencyAdapter.convertUnitsToSystemUnits(1000L, Currency("EUR")) } returns 1100L

        val useCase = IssueBonusUseCase(repository, eventAdapter, currencyAdapter)
        val command = IssueBonusUseCase.Command(
            id = "bonus-${System.nanoTime()}",
            identity = "conv-bonus",
            playerId = "player-2",
            amount = 1000L,
            currency = Currency("EUR"),
        )

        useCase(command)

        val saved = repository.findById(command.id)
        assertTrue(saved.isPresent)
        assertEquals(1100L, saved.get().amount)
    }

    // ========== LostBonusUseCase ==========

    @Test
    fun `lost bonus updates status and publishes BonusLostEvent`() = runTest {
        val bonusId = "bonus-${System.nanoTime()}"
        IssueBonusUseCase(repository, eventAdapter, currencyAdapter)(
            IssueBonusUseCase.Command(bonusId, "identity", "player-1", 5000L, Currency("USD"))
        )
        clearMocks(eventAdapter, answers = false)
        coEvery { eventAdapter.publish(any()) } just Runs

        val useCase = LostBonusUseCase(repository, eventAdapter)
        val result = useCase(LostBonusUseCase.Command(id = bonusId))

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        assertIs<BonusLostEvent>(eventSlot.captured)
        assertEquals(PlayerBonus.Status.LOST, (eventSlot.captured as BonusLostEvent).bonus.status)

        val saved = repository.findById(bonusId)
        assertTrue(saved.isPresent)
        assertEquals(PlayerBonus.Status.LOST, saved.get().status)
    }

    @Test
    fun `lost bonus fails when bonus not found`() = runTest {
        val useCase = LostBonusUseCase(repository, eventAdapter)
        val result = useCase(LostBonusUseCase.Command(id = "nonexistent"))

        assertTrue(result.isFailure)
        assertIs<NoSuchElementException>(result.exceptionOrNull())
        coVerify(exactly = 0) { eventAdapter.publish(any()) }
    }

    // ========== StartWageringBonusUseCase ==========

    @Test
    fun `start wagering updates status and publishes BonusStartWageringEvent`() = runTest {
        val bonusId = "bonus-${System.nanoTime()}"
        IssueBonusUseCase(repository, eventAdapter, currencyAdapter)(
            IssueBonusUseCase.Command(bonusId, "identity", "player-1", 5000L, Currency("USD"))
        )
        clearMocks(eventAdapter, answers = false)
        coEvery { eventAdapter.publish(any()) } just Runs

        val useCase = StartWageringBonusUseCase(repository, eventAdapter)
        val result = useCase(StartWageringBonusUseCase.Command(id = bonusId))

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        assertIs<BonusStartWageringEvent>(eventSlot.captured)
        assertEquals(PlayerBonus.Status.WAGERING, (eventSlot.captured as BonusStartWageringEvent).bonus.status)

        val saved = repository.findById(bonusId)
        assertTrue(saved.isPresent)
        assertEquals(PlayerBonus.Status.WAGERING, saved.get().status)
    }

    @Test
    fun `start wagering fails when bonus not found`() = runTest {
        val useCase = StartWageringBonusUseCase(repository, eventAdapter)
        val result = useCase(StartWageringBonusUseCase.Command(id = "nonexistent"))

        assertTrue(result.isFailure)
        assertIs<NoSuchElementException>(result.exceptionOrNull())
    }

    // ========== WageredBonusUseCase ==========

    @Test
    fun `wagered bonus updates status and payout and publishes BonusWageredEvent`() = runTest {
        val bonusId = "bonus-${System.nanoTime()}"
        IssueBonusUseCase(repository, eventAdapter, currencyAdapter)(
            IssueBonusUseCase.Command(bonusId, "identity", "player-1", 5000L, Currency("USD"))
        )
        clearMocks(eventAdapter, answers = false)
        coEvery { eventAdapter.publish(any()) } just Runs
        coEvery { currencyAdapter.convertUnitsToSystemUnits(any(), any()) } answers { firstArg() }

        val useCase = WageredBonusUseCase(repository, eventAdapter, currencyAdapter)
        val result = useCase(WageredBonusUseCase.Command(id = bonusId, payoutAmount = 3000L, currency = Currency("USD")))

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        val event = eventSlot.captured
        assertIs<BonusWageredEvent>(event)
        assertEquals(PlayerBonus.Status.WAGERED, event.bonus.status)
        assertEquals(3000L, event.bonus.payoutAmount)

        val saved = repository.findById(bonusId)
        assertTrue(saved.isPresent)
        assertEquals(PlayerBonus.Status.WAGERED, saved.get().status)
        assertEquals(3000L, saved.get().payoutAmount)
    }

    @Test
    fun `wagered bonus fails when bonus not found`() = runTest {
        val useCase = WageredBonusUseCase(repository, eventAdapter, currencyAdapter)
        val result = useCase(WageredBonusUseCase.Command(id = "nonexistent", payoutAmount = 100L, currency = Currency("USD")))

        assertTrue(result.isFailure)
        assertIs<NoSuchElementException>(result.exceptionOrNull())
    }

    @Test
    fun `event adapter failure propagates in bonus use case`() = runTest {
        coEvery { eventAdapter.publish(any()) } throws RuntimeException("broker down")

        val useCase = IssueBonusUseCase(repository, eventAdapter, currencyAdapter)
        val result = useCase(IssueBonusUseCase.Command(
            id = "bonus-${System.nanoTime()}", identity = "id", playerId = "p1", amount = 100L, currency = Currency("USD")
        ))

        assertTrue(result.isFailure)
        assertEquals("broker down", result.exceptionOrNull()?.message)
    }
}
