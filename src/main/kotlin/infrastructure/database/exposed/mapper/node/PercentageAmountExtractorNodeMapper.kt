package com.nekgambling.infrastructure.database.exposed.mapper.node

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.JourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.amount.PercentageAmountExtractor
import kotlin.reflect.KClass

object PercentageAmountExtractorNodeMapper : JourneyNodeMapper<PercentageAmountExtractor> {
    override val type: String = "percentageAmountExtractor"
    override val nodeType: KClass<PercentageAmountExtractor> = PercentageAmountExtractor::class

    override fun toDomain(
        entity: JourneyNodeEntity,
        next: IJourneyNode?,
        notMatchNode: IJourneyNode?,
    ): PercentageAmountExtractor = PercentageAmountExtractor(
        id = entity.id.value,
        next = next,
        inputKey = entity.inputKey ?: "",
        percentage = entity.percentage!!,
        maxAmount = entity.maxAmount,
    )

    override fun applyToEntity(node: PercentageAmountExtractor, entity: JourneyNodeEntity) {
        entity.inputKey = node.inputKey
        entity.percentage = node.percentage
        entity.maxAmount = node.maxAmount
    }
}
