package com.nekgambling.infrastructure.journey

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.strategy.JourneyNodeNomenclature

class NomenclatureRegistry(nomenclatures: List<JourneyNodeNomenclature<*>>) {
    private val byIdentity = nomenclatures.associateBy { it.identity }
    private val all = nomenclatures

    fun get(identity: String): JourneyNodeNomenclature<*> =
        byIdentity[identity] ?: error("No nomenclature for identity: $identity")

    @Suppress("UNCHECKED_CAST")
    fun <N : IJourneyNode> findByNode(node: N): JourneyNodeNomenclature<N> =
        all.find { it.nodeType.isInstance(node) } as? JourneyNodeNomenclature<N>
            ?: error("No nomenclature for node: ${node::class}")
}
