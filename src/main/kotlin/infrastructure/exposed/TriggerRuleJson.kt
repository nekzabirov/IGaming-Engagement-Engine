package com.nekgambling.infrastructure.exposed

import com.nekgambling.domain.trigger.model.ITriggerRule
import com.nekgambling.infrastructure.trigger.BonusTriggerRule
import com.nekgambling.infrastructure.trigger.FreespinTriggerRule
import com.nekgambling.infrastructure.trigger.InvoiceTriggerRule
import com.nekgambling.infrastructure.trigger.SegmentEnterTriggerRule
import com.nekgambling.infrastructure.trigger.SegmentExitTriggerRule
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val triggerRuleSerializersModule = SerializersModule {
    polymorphic(ITriggerRule::class) {
        subclass(InvoiceTriggerRule::class)
        subclass(BonusTriggerRule::class)
        subclass(FreespinTriggerRule::class)
        subclass(SegmentEnterTriggerRule::class)
        subclass(SegmentExitTriggerRule::class)
    }
}

val triggerRuleJson = Json {
    serializersModule = triggerRuleSerializersModule
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}
