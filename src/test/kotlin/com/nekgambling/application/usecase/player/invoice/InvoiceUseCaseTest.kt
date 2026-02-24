package com.nekgambling.application.usecase.player.invoice

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.invoice.InvoiceCreatedEvent
import com.nekgambling.application.event.player.invoice.InvoiceUpdatedEvent
import com.nekgambling.domain.player.model.PlayerInvoice
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.clickhouse.AbstractClickHouseTest
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerInvoiceRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.*

class InvoiceUseCaseTest : AbstractClickHouseTest() {

    private val repository = ClickHousePlayerInvoiceRepository(client)

    private suspend fun seedInvoice(invoiceId: String, playerId: String = "player-1"): String {
        CreateInvoiceUseCase(repository, eventAdapter, currencyAdapter)(
            playerId,
            CreateInvoiceUseCase.Transaction(
                id = invoiceId,
                type = PlayerInvoice.Type.DEPOSIT,
                currency = Currency("USD"),
                amount = 10000L,
                createdAt = Clock.System.now(),
            )
        )
        clearMocks(eventAdapter, answers = false)
        coEvery { eventAdapter.publish(any()) } just Runs
        coEvery { currencyAdapter.convertUnitsToSystemUnits(any(), any()) } answers { firstArg() }
        return invoiceId
    }

    // ========== CreateInvoiceUseCase ==========

    @Test
    fun `create invoice persists and publishes InvoiceCreatedEvent`() = runTest {
        val useCase = CreateInvoiceUseCase(repository, eventAdapter, currencyAdapter)
        val invoiceId = "inv-${System.nanoTime()}"
        val now = Clock.System.now()

        val result = useCase("player-1", CreateInvoiceUseCase.Transaction(
            id = invoiceId,
            type = PlayerInvoice.Type.DEPOSIT,
            currency = Currency("USD"),
            amount = 10000L,
            createdAt = now,
        ))

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        val event = eventSlot.captured
        assertIs<InvoiceCreatedEvent>(event)
        assertEquals(invoiceId, event.invoice.id)
        assertEquals("player-1", event.invoice.playerId)
        assertEquals(PlayerInvoice.Type.DEPOSIT, event.invoice.type)
        assertEquals(PlayerInvoice.Status.IN_PROGRESS, event.invoice.status)
        assertEquals(10000L, event.invoice.amount)
        assertEquals(0L, event.invoice.transactionAmount)
        assertEquals(0L, event.invoice.taxAmount)
        assertEquals(0L, event.invoice.feeAmount)

        val saved = repository.findById(invoiceId)
        assertTrue(saved.isPresent)
        assertEquals(PlayerInvoice.Status.IN_PROGRESS, saved.get().status)
        assertEquals(10000L, saved.get().amount)
    }

    @Test
    fun `create invoice converts currency via adapter`() = runTest {
        coEvery { currencyAdapter.convertUnitsToSystemUnits(5000L, Currency("EUR")) } returns 5500L

        val useCase = CreateInvoiceUseCase(repository, eventAdapter, currencyAdapter)
        val invoiceId = "inv-${System.nanoTime()}"

        useCase("player-1", CreateInvoiceUseCase.Transaction(
            id = invoiceId,
            type = PlayerInvoice.Type.PAYOUT,
            currency = Currency("EUR"),
            amount = 5000L,
            createdAt = Clock.System.now(),
        ))

        val saved = repository.findById(invoiceId)
        assertTrue(saved.isPresent)
        assertEquals(5500L, saved.get().amount)
    }

    @Test
    fun `create invoice with different types`() = runTest {
        val useCase = CreateInvoiceUseCase(repository, eventAdapter, currencyAdapter)

        for (type in PlayerInvoice.Type.entries) {
            val invoiceId = "inv-${type.name}-${System.nanoTime()}"
            val result = useCase("player-1", CreateInvoiceUseCase.Transaction(
                id = invoiceId,
                type = type,
                currency = Currency("USD"),
                amount = 1000L,
                createdAt = Clock.System.now(),
            ))
            assertTrue(result.isSuccess)

            val saved = repository.findById(invoiceId)
            assertTrue(saved.isPresent)
            assertEquals(type, saved.get().type)
        }
    }

    // ========== UpdateInvoiceUseCase ==========

    @Test
    fun `update invoice changes status and amounts and publishes InvoiceUpdatedEvent`() = runTest {
        val invoiceId = seedInvoice("inv-${System.nanoTime()}")

        val useCase = UpdateInvoiceUseCase(repository, eventAdapter, currencyAdapter)
        val result = useCase(invoiceId, UpdateInvoiceUseCase.Details(
            status = PlayerInvoice.Status.SUCCESS,
            transactionAmount = 9500L,
            taxAmount = 300L,
            feeAmount = 200L,
        ))

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        val event = eventSlot.captured
        assertIs<InvoiceUpdatedEvent>(event)
        assertEquals(PlayerInvoice.Status.SUCCESS, event.invoice.status)
        assertEquals(9500L, event.invoice.transactionAmount)
        assertEquals(300L, event.invoice.taxAmount)
        assertEquals(200L, event.invoice.feeAmount)

        val saved = repository.findById(invoiceId)
        assertTrue(saved.isPresent)
        assertEquals(PlayerInvoice.Status.SUCCESS, saved.get().status)
        assertEquals(9500L, saved.get().transactionAmount)
        assertEquals(300L, saved.get().taxAmount)
        assertEquals(200L, saved.get().feeAmount)
    }

    @Test
    fun `update invoice to rejected status`() = runTest {
        val invoiceId = seedInvoice("inv-${System.nanoTime()}")

        val useCase = UpdateInvoiceUseCase(repository, eventAdapter, currencyAdapter)
        val result = useCase(invoiceId, UpdateInvoiceUseCase.Details(
            status = PlayerInvoice.Status.REJECTED,
            transactionAmount = 0L,
            taxAmount = 0L,
            feeAmount = 0L,
        ))

        assertTrue(result.isSuccess)
        val saved = repository.findById(invoiceId)
        assertTrue(saved.isPresent)
        assertEquals(PlayerInvoice.Status.REJECTED, saved.get().status)
    }

    @Test
    fun `update invoice fails when invoice not found`() = runTest {
        val useCase = UpdateInvoiceUseCase(repository, eventAdapter, currencyAdapter)
        val result = useCase("nonexistent", UpdateInvoiceUseCase.Details(
            status = PlayerInvoice.Status.SUCCESS,
            transactionAmount = 100L,
            taxAmount = 0L,
            feeAmount = 0L,
        ))

        assertTrue(result.isFailure)
        assertIs<IllegalStateException>(result.exceptionOrNull())
        coVerify(exactly = 0) { eventAdapter.publish(any()) }
    }

    @Test
    fun `event adapter failure propagates in invoice use case`() = runTest {
        coEvery { eventAdapter.publish(any()) } throws RuntimeException("broker down")

        val useCase = CreateInvoiceUseCase(repository, eventAdapter, currencyAdapter)
        val result = useCase("player-1", CreateInvoiceUseCase.Transaction(
            id = "inv-${System.nanoTime()}",
            type = PlayerInvoice.Type.DEPOSIT,
            currency = Currency("USD"),
            amount = 100L,
            createdAt = Clock.System.now(),
        ))

        assertTrue(result.isFailure)
        assertEquals("broker down", result.exceptionOrNull()?.message)
    }
}
