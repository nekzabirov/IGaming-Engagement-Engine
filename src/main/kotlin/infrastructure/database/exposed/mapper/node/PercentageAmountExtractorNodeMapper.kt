package com.nekgambling.infrastructure.database.exposed.mapper.node


import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.database.exposed.entity.JourneyNodeEntity
import com.nekgambling.infrastructure.database.exposed.mapper.IJourneyNodeMapper
import com.nekgambling.infrastructure.journey.extractor.amount.PercentageAmountExtractor
import kotlin.reflect.KClass

object PercentageAmountExtractorNodeMapper : IJourneyNodeMapper<PercentageAmountExtractor> {
    override val nodeType: KClass<PercentageAmountExtractor> = PercentageAmountExtractor::class
    override val identity: String = "percentageAmount"

    override fun toDomain(entity: JourneyNodeEntity, resolveNode: (JourneyNodeEntity?) -> IJourneyNode?): PercentageAmountExtractor {
        return PercentageAmountExtractor(
            id = entity.id.value,
            next = resolveNode(entity.next),
            inputKey = entity.inputKey ?: "",
            percentage = entity.percentage!!,
            maxAmount = entity.maxAmount,
        )
    }
}
