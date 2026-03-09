package com.nekgambling.infrastructure.journey.player.invoiceTotal

import com.nekgambling.application.query.QueryBus
import com.nekgambling.application.query.player.GetPlayerInvoiceTotalQuery
import com.nekgambling.infrastructure.journey.player.IPlayerDefinitionEvaluator
import kotlin.reflect.KClass

class InvoiceTotalPlayerDefinitionEvaluator(
    private val queryBus: QueryBus,
) : IPlayerDefinitionEvaluator<InvoiceTotalPlayerDefinition> {
    override val definition: KClass<InvoiceTotalPlayerDefinition> = InvoiceTotalPlayerDefinition::class

    override suspend fun evaluate(playerId: String, condition: InvoiceTotalPlayerDefinition): Boolean {
        val total = queryBus.execute(GetPlayerInvoiceTotalQuery(playerId, condition.date.toPeriod()))

        condition.depositCount?.let {
            if (!it.check(total.depositCount)) return false
        }

        condition.withdrawCount?.let {
            if (!it.check(total.withdrawCount)) return false
        }

        condition.depositAmount?.let {
            if (!it.check(total.depositAmount)) return false
        }

        condition.withdrawAmount?.let {
            if (!it.check(total.withdrawAmount)) return false
        }

        condition.taxAmount?.let {
            if (!it.check(total.taxAmount)) return false
        }

        condition.feesAmount?.let {
            if (!it.check(total.feesAmount)) return false
        }

        return true
    }
}
