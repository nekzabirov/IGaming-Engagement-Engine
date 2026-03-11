package com.nekgambling.infrastructure.journey.extractor.player.playerProfile

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.strategy.AssetParamDescriptor
import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.domain.strategy.NodeCategory
import com.nekgambling.infrastructure.journey.extractor.player.IPlayerExtractorJourneyNode
import kotlin.reflect.KClass

object PlayerProfileExtractorNomenclature : JourneyNodeNomenclature<PlayerProfileExtractor> {
    override val nodeType: KClass<PlayerProfileExtractor> = PlayerProfileExtractor::class

    override val identity: String = "playerProfileExtractor"

    override val category: NodeCategory = NodeCategory.EXTRACTOR

    override fun inputParams(): Set<String> =
        emptySet()

    override fun outputParams(): Set<String> = setOf(
        "${IPlayerExtractorJourneyNode.PREFIX}:username",
        "${IPlayerExtractorJourneyNode.PREFIX}:email",
        "${IPlayerExtractorJourneyNode.PREFIX}:phone",
        "${IPlayerExtractorJourneyNode.PREFIX}:emailConfirmed",
        "${IPlayerExtractorJourneyNode.PREFIX}:phoneConfirmed",
        "${IPlayerExtractorJourneyNode.PREFIX}:status",
        "${IPlayerExtractorJourneyNode.PREFIX}:firstName",
        "${IPlayerExtractorJourneyNode.PREFIX}:lastName",
        "${IPlayerExtractorJourneyNode.PREFIX}:middleName",
        "${IPlayerExtractorJourneyNode.PREFIX}:birthDate",
        "${IPlayerExtractorJourneyNode.PREFIX}:country",
        "${IPlayerExtractorJourneyNode.PREFIX}:locale",
        "${IPlayerExtractorJourneyNode.PREFIX}:personalNumber",
        "${IPlayerExtractorJourneyNode.PREFIX}:isVerified",
        "${IPlayerExtractorJourneyNode.PREFIX}:gender",
        "${IPlayerExtractorJourneyNode.PREFIX}:address",
        "${IPlayerExtractorJourneyNode.PREFIX}:affiliateTag",
        "${IPlayerExtractorJourneyNode.PREFIX}:registeredAt",
    )

    override fun assetsSchema(): List<AssetParamDescriptor> = emptyList()

    override fun toAssetsMap(node: PlayerProfileExtractor): Map<String, Any> = emptyMap()

    override fun fromAssetsMap(map: Map<String, Any>): PlayerProfileExtractor = PlayerProfileExtractor()

    override fun withLinks(node: PlayerProfileExtractor, next: IJourneyNode?, matchNode: IJourneyNode?, notMatchNode: IJourneyNode?): PlayerProfileExtractor =
        node.copy(next = next)
}
