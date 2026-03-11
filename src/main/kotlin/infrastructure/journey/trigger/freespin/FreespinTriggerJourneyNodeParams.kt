package com.nekgambling.infrastructure.journey.trigger.freespin

import com.nekgambling.domain.model.player.PlayerFreespin
import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.domain.asset.NumberParamValue
import kotlin.reflect.KClass

object FreespinTriggerJourneyNodeNomenclature : JourneyNodeNomenclature<FreespinTriggerJourneyNode> {
    override val nodeType: KClass<FreespinTriggerJourneyNode> = FreespinTriggerJourneyNode::class

    override val identity: String = "freespinTrigger"

    override fun inputParams(): Set<String> =
        setOf("triggerName", "freespinId", "freespinIdentity", "gameId", "freespinCurrency", "freespinStatus")

    override fun outputParams(): Set<String> =
        setOf("freespin:id", "freespin:identity", "freespin:gameId", "freespin:currency", "freespin:status", "freespin:payoutRealAmount")

    override fun toAssetsMap(node: FreespinTriggerJourneyNode): Map<String, Any> = buildMap {
        node.freespinId?.let { put("freespinId", it) }
        node.freespinIdentity?.let { put("freespinIdentity", it) }
        node.gameId?.let { put("gameId", it) }
        node.freespinStatus?.let { put("freespinStatus", it.name) }
        node.freespinPayoutRealAmount?.let { put("freespinPayoutRealAmount", it.toMap()) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun fromAssetsMap(map: Map<String, Any>): FreespinTriggerJourneyNode = FreespinTriggerJourneyNode(
        freespinId = map["freespinId"] as? String,
        freespinIdentity = map["freespinIdentity"] as? String,
        gameId = map["gameId"] as? String,
        freespinStatus = (map["freespinStatus"] as? String)?.let { PlayerFreespin.Status.valueOf(it) },
        freespinPayoutRealAmount = (map["freespinPayoutRealAmount"] as? Map<String, Any>)?.let { NumberParamValue.fromMap(it) },
    )
}
