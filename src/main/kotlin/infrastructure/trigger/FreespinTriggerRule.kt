package com.nekgambling.infrastructure.trigger

import com.nekgambling.domain.player.model.PlayerFreespin
import com.nekgambling.domain.trigger.model.ITriggerRule
import com.nekgambling.domain.trigger.model.ITriggerRulePayload
import com.nekgambling.domain.vo.Currency
import com.nekgambling.domain.vo.param.NumberParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("freespin")
data class FreespinTriggerRule(
    val id: String? = null,
    val identity: String? = null,
    val game: String? = null,
    val status: PlayerFreespin.Status? = null,
    val payoutRealAmount: NumberParamValue? = null,
) : ITriggerRule<FreespinTriggerRule.Payload> {

    data class Payload(
        val id: String,
        val identity: String,
        val game: String,
        val currency: Currency,
        val status: PlayerFreespin.Status,
        val payoutRealAmount: Long?,
    ) : ITriggerRulePayload {
        override fun buildOutput(): Map<String, Any> = buildMap {
            put("id", id)
            put("identity", identity)
            put("game", game)
            put("currency", currency.toString())
            put("status", status.toString())
            payoutRealAmount?.let { put("payoutRealAmount", it.toString()) }
        }
    }

    override fun evaluate(payload: Payload): Boolean =
        (id == null || payload.id == id)
                && (identity == null || payload.identity == identity)
                && (game == null || payload.game == game)
                && (status == null || payload.status == status)
                && (payoutRealAmount == null || payload.payoutRealAmount != null && payoutRealAmount.check(payload.payoutRealAmount))
}
