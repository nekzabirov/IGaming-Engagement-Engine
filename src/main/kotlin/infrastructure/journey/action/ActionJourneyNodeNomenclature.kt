package com.nekgambling.infrastructure.journey.action

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.domain.strategy.NodeCategory

abstract class ActionJourneyNodeNomenclature<N : IJourneyNode> : JourneyNodeNomenclature<N> {
    override val category: NodeCategory = NodeCategory.ACTION
}
