package com.nekgambling.infrastructure.journey.trigger.bonus

import com.nekgambling.domain.model.player.PlayerBonus
import com.nekgambling.domain.strategy.*
import com.nekgambling.domain.asset.NumberParamValue
import kotlin.reflect.KClass

object BonusTriggerJourneyNodeNomenclature : JourneyNodeNomenclature<BonusTriggerJourneyNode> {
    override val nodeType: KClass<BonusTriggerJourneyNode> = BonusTriggerJourneyNode::class

    override val identity: String = "bonusTrigger"

    override val category: NodeCategory = NodeCategory.TRIGGER

    override fun inputParams(): Set<String> =
        setOf("triggerName", "bonusId", "bonusIdentity", "bonusStatus", "bonusPayoutAmount")

    override fun outputParams(): Set<String> =
        setOf("bonus:id", "bonus:identity", "bonus:status", "bonus:payoutAmount")

    override fun assetsSchema(): List<AssetParamDescriptor> = listOf(
        AssetParamDescriptor(name = "bonusId", type = ParamType.STRING, required = false),
        AssetParamDescriptor(name = "bonusIdentity", type = ParamType.STRING, required = false),
        AssetParamDescriptor(
            name = "bonusStatus", type = ParamType.ENUM, required = false,
            enumValues = PlayerBonus.Status.entries.map { it.name },
        ),
        AssetParamDescriptor(
            name = "bonusPayoutAmount", type = ParamType.OBJECT, required = false,
            subtypes = numberParamValueSubtypes(),
        ),
    )

    override fun toAssetsMap(node: BonusTriggerJourneyNode): Map<String, Any> = buildMap {
        node.bonusId?.let { put("bonusId", it) }
        node.bonusIdentity?.let { put("bonusIdentity", it) }
        node.bonusStatus?.let { put("bonusStatus", it.name) }
        node.bonusPayoutAmount?.let { put("bonusPayoutAmount", it.toMap()) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun fromAssetsMap(map: Map<String, Any>): BonusTriggerJourneyNode = BonusTriggerJourneyNode(
        bonusId = map["bonusId"] as? String,
        bonusIdentity = map["bonusIdentity"] as? String,
        bonusStatus = (map["bonusStatus"] as? String)?.let { PlayerBonus.Status.valueOf(it) },
        bonusPayoutAmount = (map["bonusPayoutAmount"] as? Map<String, Any>)?.let { NumberParamValue.fromMap(it) },
    )
}
