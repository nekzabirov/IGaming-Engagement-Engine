package com.nekgambling.application.usecase.player.freespin

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.freespin.FreespinActivatedEvent
import com.nekgambling.application.event.player.freespin.FreespinCanceledEvent
import com.nekgambling.application.event.player.freespin.FreespinIssuedEvent
import com.nekgambling.application.event.player.freespin.FreespinPlayedEvent
import com.nekgambling.domain.player.model.PlayerFreespin
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.clickhouse.AbstractClickHouseTest
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerFreespinRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class FreespinUseCaseTest : AbstractClickHouseTest() {

    private val repository = ClickHousePlayerFreespinRepository(client)

    private suspend fun seedFreespin(playerId: String, freespinId: String, game: String = "slots-gold") {
        IssueFreespinUseCase(repository, eventAdapter)(
            IssueFreespinUseCase.Command(id = freespinId, identity = "promo-1", playerId = playerId, game = game)
        )
        clearMocks(eventAdapter, answers = false)
        coEvery { eventAdapter.publish(any()) } just Runs
    }

    // ========== IssueFreespinUseCase ==========

    @Test
    fun `issue freespin creates freespin and publishes FreespinIssuedEvent`() = runTest {
        val useCase = IssueFreespinUseCase(repository, eventAdapter)
        val command = IssueFreespinUseCase.Command(
            id = "fs-${System.nanoTime()}",
            identity = "promo-welcome",
            playerId = "player-1",
            game = "slots-gold",
        )

        val result = useCase(command)

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        val event = eventSlot.captured
        assertIs<FreespinIssuedEvent>(event)
        assertEquals(command.id, event.freespin.id)
        assertEquals("promo-welcome", event.freespin.identity)
        assertEquals("player-1", event.freespin.playerId)
        assertEquals("slots-gold", event.freespin.game)
        assertEquals(PlayerFreespin.Status.ISSUE, event.freespin.status)
        assertEquals(0L, event.freespin.payoutRealAmount)

        val saved = repository.findBy("player-1", command.id)
        assertTrue(saved.isPresent)
        assertEquals(PlayerFreespin.Status.ISSUE, saved.get().status)
    }

    // ========== ActivateFreespinUseCase ==========

    @Test
    fun `activate freespin updates status and publishes FreespinActivatedEvent`() = runTest {
        val playerId = "player-1"
        val freespinId = "fs-${System.nanoTime()}"
        seedFreespin(playerId, freespinId)

        val useCase = ActivateFreespinUseCase(repository, eventAdapter)
        val result = useCase(ActivateFreespinUseCase.Command(playerId = playerId, freespinId = freespinId))

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        assertIs<FreespinActivatedEvent>(eventSlot.captured)
        assertEquals(PlayerFreespin.Status.ACTIVE, (eventSlot.captured as FreespinActivatedEvent).freespin.status)

        val saved = repository.findBy(playerId, freespinId)
        assertTrue(saved.isPresent)
        assertEquals(PlayerFreespin.Status.ACTIVE, saved.get().status)
    }

    @Test
    fun `activate freespin fails when not found`() = runTest {
        val useCase = ActivateFreespinUseCase(repository, eventAdapter)
        val result = useCase(ActivateFreespinUseCase.Command(playerId = "x", freespinId = "x"))

        assertTrue(result.isFailure)
        assertIs<NoSuchElementException>(result.exceptionOrNull())
        coVerify(exactly = 0) { eventAdapter.publish(any()) }
    }

    // ========== CancelFreespinUseCase ==========

    @Test
    fun `cancel freespin updates status and publishes FreespinCanceledEvent`() = runTest {
        val playerId = "player-1"
        val freespinId = "fs-${System.nanoTime()}"
        seedFreespin(playerId, freespinId)

        val useCase = CancelFreespinUseCase(repository, eventAdapter)
        val result = useCase(CancelFreespinUseCase.Command(playerId = playerId, freespinId = freespinId))

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        assertIs<FreespinCanceledEvent>(eventSlot.captured)
        assertEquals(PlayerFreespin.Status.CANCELLED, (eventSlot.captured as FreespinCanceledEvent).freespin.status)

        val saved = repository.findBy(playerId, freespinId)
        assertTrue(saved.isPresent)
        assertEquals(PlayerFreespin.Status.CANCELLED, saved.get().status)
    }

    @Test
    fun `cancel freespin fails when not found`() = runTest {
        val useCase = CancelFreespinUseCase(repository, eventAdapter)
        val result = useCase(CancelFreespinUseCase.Command(playerId = "x", freespinId = "x"))

        assertTrue(result.isFailure)
        assertIs<NoSuchElementException>(result.exceptionOrNull())
    }

    // ========== FinishFreespinUseCase ==========

    @Test
    fun `finish freespin updates status and payout and publishes FreespinPlayedEvent`() = runTest {
        val playerId = "player-1"
        val freespinId = "fs-${System.nanoTime()}"
        seedFreespin(playerId, freespinId)

        val useCase = FinishFreespinUseCase(repository, eventAdapter, currencyAdapter)
        val result = useCase(FinishFreespinUseCase.Command(
            playerId = playerId, freespinId = freespinId, payoutRealAmount = 7500L, currency = Currency("USD")
        ))

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        val event = eventSlot.captured
        assertIs<FreespinPlayedEvent>(event)
        assertEquals(PlayerFreespin.Status.PLAYED, event.freespin.status)
        assertEquals(7500L, event.freespin.payoutRealAmount)

        val saved = repository.findBy(playerId, freespinId)
        assertTrue(saved.isPresent)
        assertEquals(PlayerFreespin.Status.PLAYED, saved.get().status)
        assertEquals(7500L, saved.get().payoutRealAmount)
    }

    @Test
    fun `finish freespin fails when not found`() = runTest {
        val useCase = FinishFreespinUseCase(repository, eventAdapter, currencyAdapter)
        val result = useCase(FinishFreespinUseCase.Command(
            playerId = "x", freespinId = "x", payoutRealAmount = 100L, currency = Currency("USD")
        ))

        assertTrue(result.isFailure)
        assertIs<NoSuchElementException>(result.exceptionOrNull())
    }

    @Test
    fun `event adapter failure propagates in freespin use case`() = runTest {
        coEvery { eventAdapter.publish(any()) } throws RuntimeException("broker down")

        val useCase = IssueFreespinUseCase(repository, eventAdapter)
        val result = useCase(IssueFreespinUseCase.Command(
            id = "fs-${System.nanoTime()}", identity = "id", playerId = "p1", game = "game"
        ))

        assertTrue(result.isFailure)
        assertEquals("broker down", result.exceptionOrNull()?.message)
    }
}
