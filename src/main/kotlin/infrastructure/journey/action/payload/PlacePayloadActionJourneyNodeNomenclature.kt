package com.nekgambling.infrastructure.journey.action.payload

import com.nekgambling.infrastructure.journey.action.ActionJourneyNodeNomenclature
import kotlin.reflect.KClass

object PlacePayloadActionJourneyNodeNomenclature : ActionJourneyNodeNomenclature<PlacePayloadActionJourneyNode>() {
    override val nodeType: KClass<PlacePayloadActionJourneyNode> = PlacePayloadActionJourneyNode::class

    override fun inputParams(): Set<String> = emptySet()

    override fun outputParams(): Set<String> = emptySet()
}
