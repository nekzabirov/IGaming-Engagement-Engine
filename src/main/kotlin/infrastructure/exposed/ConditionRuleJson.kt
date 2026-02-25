package com.nekgambling.infrastructure.exposed

import com.nekgambling.domain.condition.model.IConditionRule
import com.nekgambling.infrastructure.condition.invoiceTotal.InvoiceTotalConditionRule
import com.nekgambling.infrastructure.condition.playerAge.PlayerAgeConditionRule
import com.nekgambling.infrastructure.condition.playerGGR.PlayerGgrConditionRule
import com.nekgambling.infrastructure.condition.profile.ProfileFieldConditionRule
import com.nekgambling.infrastructure.condition.spinTotal.SpinTotalConditionRule
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val conditionRuleSerializersModule = SerializersModule {
    polymorphic(IConditionRule::class) {
        subclass(PlayerAgeConditionRule::class)
        subclass(ProfileFieldConditionRule::class)
        subclass(SpinTotalConditionRule::class)
        subclass(InvoiceTotalConditionRule::class)
        subclass(PlayerGgrConditionRule::class)
    }
}

val conditionRuleJson = Json {
    serializersModule = conditionRuleSerializersModule
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}
