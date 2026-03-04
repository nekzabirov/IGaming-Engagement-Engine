package com.nekgambling.domain.trigger.model

interface ITriggerRulePayload {
    fun buildOutput(): Map<String, Any>
}

interface ITriggerRule<P : ITriggerRulePayload> {
    fun evaluate(payload: P): Boolean
}