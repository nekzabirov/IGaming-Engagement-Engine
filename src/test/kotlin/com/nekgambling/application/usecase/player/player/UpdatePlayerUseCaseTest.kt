package com.nekgambling.application.usecase.player.player

import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.event.player.player.PlayerRegisteredEvent
import com.nekgambling.application.event.player.player.PlayerUpdatedEvent
import com.nekgambling.domain.player.model.PlayerDetails
import com.nekgambling.domain.vo.Country
import com.nekgambling.domain.vo.Locale
import com.nekgambling.infrastructure.clickhouse.AbstractClickHouseTest
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerDetailsRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.*

class UpdatePlayerUseCaseTest : AbstractClickHouseTest() {

    private val repository = ClickHousePlayerDetailsRepository(client)
    private val useCase = UpdatePlayerUseCase(repository, eventAdapter)

    @Test
    fun `new player is created and PlayerRegisteredEvent is published`() = runTest {
        val playerId = "player-${System.nanoTime()}"
        val now = Clock.System.now()

        val details = UpdatePlayerUseCase.Details(
            username = "testuser",
            email = "test@example.com",
            phone = "+1234567890",
            emailConfirmed = true,
            phoneConfirmed = false,
            firstName = "John",
            lastName = "Doe",
            country = Country("US"),
            locale = Locale("en"),
            isVerified = false,
            gender = PlayerDetails.Gender.MALE,
            registeredAt = now,
        )

        val result = useCase(playerId, details)

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        val event = eventSlot.captured
        assertIs<PlayerRegisteredEvent>(event)
        assertEquals(playerId, event.playerId)
        assertEquals("testuser", event.details.username)
        assertEquals("test@example.com", event.details.email)

        val saved = repository.findById(playerId)
        assertTrue(saved.isPresent)
        val player = saved.get()
        assertEquals("testuser", player.username)
        assertEquals("test@example.com", player.email)
        assertEquals("+1234567890", player.phone)
        assertTrue(player.emailConfirmed)
        assertFalse(player.phoneConfirmed)
        assertEquals(PlayerDetails.Status.ACTIVE, player.status)
        assertEquals("John", player.firstName)
        assertEquals("Doe", player.lastName)
        assertEquals(Country("US"), player.country)
        assertEquals(Locale("en"), player.locale)
        assertEquals(PlayerDetails.Gender.MALE, player.gender)
        assertFalse(player.isVerified)
    }

    @Test
    fun `existing player is updated and PlayerUpdatedEvent is published`() = runTest {
        val playerId = "player-${System.nanoTime()}"
        val now = Clock.System.now()

        val createDetails = UpdatePlayerUseCase.Details(
            username = "original",
            email = "original@example.com",
            emailConfirmed = false,
            phoneConfirmed = false,
            isVerified = false,
            registeredAt = now,
        )
        useCase(playerId, createDetails)
        clearMocks(eventAdapter, answers = false)
        coEvery { eventAdapter.publish(any()) } just Runs

        val updateDetails = UpdatePlayerUseCase.Details(
            username = "updated",
            email = "updated@example.com",
            firstName = "Jane",
            country = Country("GB"),
        )
        val result = useCase(playerId, updateDetails)

        assertTrue(result.isSuccess)

        val eventSlot = slot<IEventAdapter.AppEvent>()
        coVerify(exactly = 1) { eventAdapter.publish(capture(eventSlot)) }
        val event = eventSlot.captured
        assertIs<PlayerUpdatedEvent>(event)
        assertEquals(playerId, event.playerId)
        assertEquals("updated", event.details.username)
        assertEquals("updated@example.com", event.details.email)
        assertEquals("Jane", event.details.firstName)
        assertEquals(Country("GB"), event.details.country)
    }

    @Test
    fun `new player without registeredAt fails`() = runTest {
        val playerId = "player-${System.nanoTime()}"

        val details = UpdatePlayerUseCase.Details(
            username = "testuser",
            emailConfirmed = false,
            phoneConfirmed = false,
            isVerified = false,
        )

        val result = useCase(playerId, details)

        assertTrue(result.isFailure)
        assertIs<IllegalArgumentException>(result.exceptionOrNull())
        coVerify(exactly = 0) { eventAdapter.publish(any()) }
    }

    @Test
    fun `update preserves fields not included in details`() = runTest {
        val playerId = "player-${System.nanoTime()}"
        val now = Clock.System.now()

        useCase(playerId, UpdatePlayerUseCase.Details(
            username = "keeper",
            email = "keep@example.com",
            phone = "+9876543210",
            emailConfirmed = true,
            phoneConfirmed = true,
            firstName = "Keep",
            lastName = "Me",
            country = Country("DE"),
            locale = Locale("de"),
            isVerified = true,
            gender = PlayerDetails.Gender.FEMALE,
            address = "Berlin",
            affiliateTag = "aff-123",
            registeredAt = now,
        ))
        clearMocks(eventAdapter, answers = false)
        coEvery { eventAdapter.publish(any()) } just Runs

        useCase(playerId, UpdatePlayerUseCase.Details(username = "newname"))

        val saved = repository.findById(playerId)
        assertTrue(saved.isPresent)
        val player = saved.get()
        assertEquals("newname", player.username)
        assertEquals("keep@example.com", player.email)
        assertEquals("+9876543210", player.phone)
        assertEquals("Keep", player.firstName)
        assertEquals("Me", player.lastName)
        assertEquals(Country("DE"), player.country)
        assertEquals("Berlin", player.address)
        assertEquals("aff-123", player.affiliateTag)
    }

    @Test
    fun `event adapter failure propagates as failure result`() = runTest {
        val playerId = "player-${System.nanoTime()}"
        val now = Clock.System.now()

        coEvery { eventAdapter.publish(any()) } throws RuntimeException("broker down")

        val details = UpdatePlayerUseCase.Details(
            username = "failuser",
            emailConfirmed = false,
            phoneConfirmed = false,
            isVerified = false,
            registeredAt = now,
        )

        val result = useCase(playerId, details)

        assertTrue(result.isFailure)
        assertEquals("broker down", result.exceptionOrNull()?.message)
    }
}
