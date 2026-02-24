package com.nekgambling.infrastructure.clickhouse.query

import com.nekgambling.application.query.player.GetPlayerBonusPayoutTotalQuery
import com.nekgambling.application.usecase.player.bonus.IssueBonusUseCase
import com.nekgambling.application.usecase.player.bonus.WageredBonusUseCase
import com.nekgambling.application.usecase.player.freespin.FinishFreespinUseCase
import com.nekgambling.application.usecase.player.freespin.IssueFreespinUseCase
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.vo.Period
import com.nekgambling.infrastructure.clickhouse.AbstractClickHouseTest
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerBonusRepository
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerFreespinRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.*
import kotlin.time.Duration.Companion.days

class PlayerBonusPayoutTotalQueryHandlerTest : AbstractClickHouseTest() {

    private val bonusRepository = ClickHousePlayerBonusRepository(client)
    private val freespinRepository = ClickHousePlayerFreespinRepository(client)
    private val handler = ClickHousePlayerBonusPayoutTotalQueryHandler(client)

    private fun todayPeriod(): Period = Pair(
        Clock.System.now().minus(1.days),
        Clock.System.now().plus(1.days),
    )

    private suspend fun issueBonusWithPayout(
        bonusId: String,
        playerId: String = "player-1",
        payoutAmount: Long,
    ) {
        IssueBonusUseCase(bonusRepository, eventAdapter, currencyAdapter)(
            IssueBonusUseCase.Command(bonusId, "identity", playerId, 5000L, Currency("USD"))
        )
        WageredBonusUseCase(bonusRepository, eventAdapter, currencyAdapter)(
            WageredBonusUseCase.Command(bonusId, payoutAmount, Currency("USD"))
        )
    }

    private suspend fun issueFreespinWithPayout(
        freespinId: String,
        playerId: String = "player-1",
        payoutAmount: Long,
    ) {
        IssueFreespinUseCase(freespinRepository, eventAdapter)(
            IssueFreespinUseCase.Command(freespinId, "promo-1", playerId, "slots-gold")
        )
        FinishFreespinUseCase(freespinRepository, eventAdapter, currencyAdapter)(
            FinishFreespinUseCase.Command(playerId, freespinId, payoutAmount, Currency("USD"))
        )
    }

    @Test
    fun `returns zero when no data exists`() = runTest {
        val result = handler.handle(GetPlayerBonusPayoutTotalQuery("player-1", todayPeriod()))
        assertEquals(0L, result)
    }

    @Test
    fun `returns correct bonus payout total`() = runTest {
        issueBonusWithPayout("bonus-1", payoutAmount = 3000L)
        issueBonusWithPayout("bonus-2", payoutAmount = 2000L)

        val result = handler.handle(GetPlayerBonusPayoutTotalQuery("player-1", todayPeriod()))
        assertEquals(5000L, result)
    }

    @Test
    fun `returns correct freespin payout total`() = runTest {
        issueFreespinWithPayout("fs-1", payoutAmount = 7500L)

        val result = handler.handle(GetPlayerBonusPayoutTotalQuery("player-1", todayPeriod()))
        assertEquals(7500L, result)
    }

    @Test
    fun `returns combined total from bonuses and freespins`() = runTest {
        issueBonusWithPayout("bonus-1", payoutAmount = 3000L)
        issueFreespinWithPayout("fs-1", payoutAmount = 7500L)

        val result = handler.handle(GetPlayerBonusPayoutTotalQuery("player-1", todayPeriod()))
        assertEquals(10500L, result)
    }

    @Test
    fun `filters by player_id`() = runTest {
        issueBonusWithPayout("bonus-1", playerId = "player-1", payoutAmount = 3000L)
        issueBonusWithPayout("bonus-2", playerId = "player-2", payoutAmount = 2000L)

        assertEquals(3000L, handler.handle(GetPlayerBonusPayoutTotalQuery("player-1", todayPeriod())))
        assertEquals(2000L, handler.handle(GetPlayerBonusPayoutTotalQuery("player-2", todayPeriod())))
    }

    @Test
    fun `bonus without payout does not contribute to total`() = runTest {
        // Only issue, don't wagered - payout stays at 0
        IssueBonusUseCase(bonusRepository, eventAdapter, currencyAdapter)(
            IssueBonusUseCase.Command("bonus-1", "identity", "player-1", 5000L, Currency("USD"))
        )

        val result = handler.handle(GetPlayerBonusPayoutTotalQuery("player-1", todayPeriod()))
        assertEquals(0L, result)
    }
}
