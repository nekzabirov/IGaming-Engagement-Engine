package com.nekgambling.domain.model.journey

import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueDynamicBonusActionJourneyNode
import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueFixedBonusActionJourneyNode
import com.nekgambling.infrastructure.journey.action.issue.freespin.IssueFreespinActionJourneyNode
import com.nekgambling.infrastructure.journey.action.payload.PlacePayloadActionJourneyNode
import com.nekgambling.infrastructure.journey.action.push.EMailPushActionJourneyNode
import com.nekgambling.infrastructure.journey.action.push.InAppPushActionJourneyNode
import com.nekgambling.infrastructure.journey.action.push.SmsPushActionJourneyNode
import com.nekgambling.infrastructure.journey.condition.BoolConditionJourneyNode
import com.nekgambling.infrastructure.journey.condition.NumberEqualConditionJourneyNode
import com.nekgambling.infrastructure.journey.condition.NumberInRangeConditionJourneyNode
import com.nekgambling.infrastructure.journey.condition.NumberLessThanConditionJourneyNode
import com.nekgambling.infrastructure.journey.condition.NumberMoreThanConditionJourneyNode
import com.nekgambling.infrastructure.journey.condition.StringEqualConditionJourneyNode
import com.nekgambling.infrastructure.journey.extractor.amount.PercentageAmountExtractor
import com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal.InvoiceTotalExtractor
import com.nekgambling.infrastructure.journey.extractor.player.playerAge.PlayerAgeExtractor
import com.nekgambling.infrastructure.journey.extractor.player.playerGgr.PlayerGgrExtractor
import com.nekgambling.infrastructure.journey.extractor.player.playerProfile.PlayerProfileExtractor
import com.nekgambling.infrastructure.journey.extractor.player.spinTotal.SpinTotalExtractor
import com.nekgambling.infrastructure.journey.trigger.bonus.BonusTriggerJourneyNode
import com.nekgambling.infrastructure.journey.trigger.freespin.FreespinTriggerJourneyNode
import com.nekgambling.infrastructure.journey.trigger.invoice.InvoiceTriggerJourneyNode
import com.nekgambling.infrastructure.journey.trigger.segment.SegmentTriggerJourneyNode
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val JourneyNodeSerializersModule = SerializersModule {
    polymorphic(IJourneyNode::class) {
        // Triggers
        subclass(BonusTriggerJourneyNode::class)
        subclass(FreespinTriggerJourneyNode::class)
        subclass(InvoiceTriggerJourneyNode::class)
        subclass(SegmentTriggerJourneyNode::class)

        // Actions - Push
        subclass(EMailPushActionJourneyNode::class)
        subclass(SmsPushActionJourneyNode::class)
        subclass(InAppPushActionJourneyNode::class)

        // Actions - Issue
        subclass(IssueFixedBonusActionJourneyNode::class)
        subclass(IssueDynamicBonusActionJourneyNode::class)
        subclass(IssueFreespinActionJourneyNode::class)

        // Actions - Payload
        subclass(PlacePayloadActionJourneyNode::class)

        // Conditions - Number
        subclass(NumberInRangeConditionJourneyNode::class)
        subclass(NumberMoreThanConditionJourneyNode::class)
        subclass(NumberLessThanConditionJourneyNode::class)
        subclass(NumberEqualConditionJourneyNode::class)

        // Conditions - String
        subclass(StringEqualConditionJourneyNode::class)

        // Conditions - Bool
        subclass(BoolConditionJourneyNode::class)

        // Extractors - Amount
        subclass(PercentageAmountExtractor::class)

        // Extractors - Player
        subclass(PlayerProfileExtractor::class)
        subclass(PlayerAgeExtractor::class)
        subclass(InvoiceTotalExtractor::class)
        subclass(SpinTotalExtractor::class)
        subclass(PlayerGgrExtractor::class)
    }
}
