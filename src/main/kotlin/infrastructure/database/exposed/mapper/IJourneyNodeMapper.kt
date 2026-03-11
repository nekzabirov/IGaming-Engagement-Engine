package com.nekgambling.infrastructure.database.exposed.mapper

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import kotlin.reflect.KClass

interface IJourneyNodeMapper<T : IJourneyNode> {
    val nodeType: KClass<T>

    val identity: String

    fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): T

    fun applyToEntity(entity: JourneyNodeEntity, node: T)
}