package com.nekgambling.infrastructure.trigger

import com.nekgambling.domain.player.model.PlayerBonus
import com.nekgambling.domain.trigger.model.ITriggerRule
import com.nekgambling.domain.trigger.model.ITriggerRulePayload
import com.nekgambling.domain.vo.param.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("bonus")
data class BonusTriggerRule(
    val id: String? = null,
    val identity: String? = null,
    val status: PlayerBonus.Status? = null,
    val payoutAmount: NumberParamValue? = null,
) : ITriggerRule<BonusTriggerRule.Payload> {

    data class Payload(
        val id: String,
        val identity: String,
        val status: PlayerBonus.Status,
        val payoutAmount: Long,
    ) : ITriggerRulePayload {
        override fun buildOutput(): Map<String, Any> = mapOf(
            "id" to id,
            "identity" to identity,
            "status" to status.toString(),
            "payoutAmount" to payoutAmount.toString(),
        )
    }

    override fun evaluate(payload: Payload): Boolean =
        (id == null || payload.id == id)
                && (identity == null || payload.identity == identity)
                && (status == null || payload.status == status)
                && (payoutAmount == null || payoutAmount.check(payload.payoutAmount))
}
