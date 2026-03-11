package com.nekgambling.infrastructure.database.exposed.mapper

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import kotlin.reflect.KClass

interface JourneyNodeMapper<N : IJourneyNode> {
    val type: String
    val nodeType: KClass<out N>

    fun toDomain(entity: JourneyNodeEntity, next: IJourneyNode?, notMatchNode: IJourneyNode?): N

    fun applyToEntity(node: N, entity: JourneyNodeEntity)
}
