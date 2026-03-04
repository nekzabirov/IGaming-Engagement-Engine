package com.nekgambling.infrastructure.trigger

import com.nekgambling.domain.trigger.model.ITriggerRule
import com.nekgambling.domain.trigger.model.ITriggerRulePayload
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("segmentEnter")
data class SegmentEnterTriggerRule(
    val identity: String? = null,
) : ITriggerRule<SegmentEnterTriggerRule.Payload> {

    data class Payload(
        val segmentIdentity: String,
    ) : ITriggerRulePayload {
        override fun buildOutput(): Map<String, Any> = mapOf(
            "segmentIdentity" to segmentIdentity,
        )
    }

    override fun evaluate(payload: Payload): Boolean =
        identity == null || payload.segmentIdentity == identity
}
