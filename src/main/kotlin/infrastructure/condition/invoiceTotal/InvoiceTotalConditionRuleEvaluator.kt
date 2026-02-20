package com.nekgambling.infrastructure.condition.invoiceTotal

import com.nekgambling.application.reader.IPlayerInvoiceTotalReader
import com.nekgambling.domain.condition.strategy.IConditionRuleEvaluator
import kotlin.reflect.KClass

class InvoiceTotalConditionRuleEvaluator(
    private val playerInvoiceTotalReader: IPlayerInvoiceTotalReader
) : IConditionRuleEvaluator<InvoiceTotalConditionRule> {
    override val condition: KClass<InvoiceTotalConditionRule> = InvoiceTotalConditionRule::class

    override suspend fun evaluate(playerId: String, condition: InvoiceTotalConditionRule): Boolean {
        val total = playerInvoiceTotalReader.read(playerId, condition.date.toPeriod())

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
