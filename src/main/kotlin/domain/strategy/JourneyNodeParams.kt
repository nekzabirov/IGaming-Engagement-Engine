package com.nekgambling.domain.strategy

import com.nekgambling.domain.model.journey.IJourneyNode
import kotlin.reflect.KClass

interface JourneyNodeParams<N: IJourneyNode> {
    val nodeType: KClass<N>

    fun inputParams(): Set<String>

    fun outputParams(): Set<String>
}
