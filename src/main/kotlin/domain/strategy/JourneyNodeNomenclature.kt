package com.nekgambling.domain.strategy

import com.nekgambling.domain.model.journey.IJourneyNode
import kotlin.reflect.KClass

interface JourneyNodeNomenclature<N: IJourneyNode> {
    val nodeType: KClass<N>

    val identity: String

    fun inputParams(): Set<String>

    fun outputParams(): Set<String>

    fun toAssetsMap(node: N): Map<String, Any>

    fun fromAssetsMap(map: Map<String, Any>): N
}
