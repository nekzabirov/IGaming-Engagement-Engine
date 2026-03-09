package com.nekgambling.infrastructure.database.exposed.mapper

import com.nekgambling.infrastructure.journey.player.IPlayerDefinition
import com.nekgambling.infrastructure.journey.player.invoiceTotal.InvoiceTotalPlayerDefinition
import com.nekgambling.infrastructure.journey.player.playerAge.PlayerAgeDefinition
import com.nekgambling.infrastructure.journey.player.playerGGR.PlayerGgrDefinition
import com.nekgambling.infrastructure.journey.player.profile.ProfileFieldPlayerDefinition
import com.nekgambling.infrastructure.journey.player.spinTotal.SpinTotalPlayerDefinition
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val conditionRuleSerializersModule = SerializersModule {
    polymorphic(IPlayerDefinition::class) {
        subclass(PlayerAgeDefinition::class)
        subclass(ProfileFieldPlayerDefinition::class)
        subclass(SpinTotalPlayerDefinition::class)
        subclass(InvoiceTotalPlayerDefinition::class)
        subclass(PlayerGgrDefinition::class)
    }
}

val conditionRuleJson = Json {
    serializersModule = conditionRuleSerializersModule
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}
