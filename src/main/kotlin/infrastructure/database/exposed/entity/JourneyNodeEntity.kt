package com.nekgambling.infrastructure.database.exposed.entity

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.model.player.PlayerBonus
import com.nekgambling.domain.model.player.PlayerFreespin
import com.nekgambling.domain.model.player.PlayerInvoice
import com.nekgambling.domain.vo.Currency
import com.nekgambling.infrastructure.database.exposed.table.JourneyNodesTable
import com.nekgambling.infrastructure.journey.player.PlayerJourneyNode
import com.nekgambling.infrastructure.journey.trigger.bonus.BonusTriggerJourneyNode
import com.nekgambling.infrastructure.journey.trigger.freespin.FreespinTriggerJourneyNode
import com.nekgambling.infrastructure.journey.trigger.invoice.InvoiceTriggerJourneyNode
import com.nekgambling.infrastructure.journey.action.issue.freespin.IssueFreespinActionJourneyNode
import com.nekgambling.infrastructure.journey.action.payload.PlacePayloadActionJourneyNode
import com.nekgambling.infrastructure.journey.trigger.segment.SegmentTriggerJourneyNode
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class JourneyNodeEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<JourneyNodeEntity>(JourneyNodesTable)

    var type by JourneyNodesTable.type
    var journey by JourneyEntity referencedOn JourneyNodesTable.journeyId
    var next by JourneyNodeEntity optionalReferencedOn JourneyNodesTable.next

    // PlayerJourneyNode
    var playerDefinition by JourneyNodesTable.playerDefinition
    var onMismatch by JourneyNodeEntity optionalReferencedOn JourneyNodesTable.onMismatch

    // BonusTriggerJourneyNode
    var bonusId by JourneyNodesTable.bonusId
    var bonusIdentity by JourneyNodesTable.bonusIdentity
    var bonusStatus by JourneyNodesTable.bonusStatus
    var bonusPayoutAmount by JourneyNodesTable.bonusPayoutAmount

    // FreespinTriggerJourneyNode
    var freespinId by JourneyNodesTable.freespinId
    var freespinIdentity by JourneyNodesTable.freespinIdentity
    var gameId by JourneyNodesTable.gameId
    var freespinStatus by JourneyNodesTable.freespinStatus
    var freespinPayoutRealAmount by JourneyNodesTable.freespinPayoutRealAmount

    // InvoiceTriggerJourneyNode
    var invoiceType by JourneyNodesTable.invoiceType
    var invoiceStatus by JourneyNodesTable.invoiceStatus
    var invoiceCurrency by JourneyNodesTable.invoiceCurrency
    var invoiceAmount by JourneyNodesTable.invoiceAmount

    // SegmentTriggerJourneyNode
    var segmentType by JourneyNodesTable.segmentType
    var segmentIdentity by JourneyNodesTable.segmentIdentity

    // IssueFreespinActionJourneyNode
    var issueFreespinIdentity by JourneyNodesTable.issueFreespinIdentity

    // PlacePayloadActionJourneyNode
    var placePayloadKey by JourneyNodesTable.placePayloadKey
    var placePayloadValue by JourneyNodesTable.placePayloadValue

    fun toDomain(cache: MutableMap<Long, IJourneyNode> = mutableMapOf()): IJourneyNode {
        cache[id.value]?.let { return it }

        val nextDomain = next?.toDomain(cache)

        val node: IJourneyNode = when (type) {
            PlayerJourneyNode::class.simpleName -> {
                val onMismatchDomain = onMismatch?.toDomain(cache)
                PlayerJourneyNode(
                    id = id.value,
                    rule = playerDefinition!!,
                    matchNode = nextDomain,
                    notMatchNode = onMismatchDomain,
                )
            }

            BonusTriggerJourneyNode::class.simpleName -> BonusTriggerJourneyNode(
                id = id.value,
                bonusId = bonusId,
                bonusIdentity = bonusIdentity,
                bonusStatus = bonusStatus?.let { PlayerBonus.Status.valueOf(it) },
                bonusPayoutAmount = bonusPayoutAmount,
                next = nextDomain,
            )

            FreespinTriggerJourneyNode::class.simpleName -> FreespinTriggerJourneyNode(
                id = id.value,
                freespinId = freespinId,
                freespinIdentity = freespinIdentity,
                gameId = gameId,
                freespinStatus = freespinStatus?.let { PlayerFreespin.Status.valueOf(it) },
                freespinPayoutRealAmount = freespinPayoutRealAmount,
                next = nextDomain,
            )

            InvoiceTriggerJourneyNode::class.simpleName -> InvoiceTriggerJourneyNode(
                id = id.value,
                invoiceType = PlayerInvoice.Type.valueOf(invoiceType!!),
                invoiceStatus = PlayerInvoice.Status.valueOf(invoiceStatus!!),
                invoiceCurrency = invoiceCurrency?.let { Currency(it) },
                invoiceAmount = invoiceAmount,
                next = nextDomain,
            )

            SegmentTriggerJourneyNode::class.simpleName -> SegmentTriggerJourneyNode(
                id = id.value,
                type = SegmentTriggerJourneyNode.Type.valueOf(segmentType!!),
                segment = segmentIdentity,
                next = nextDomain,
            )

            IssueFreespinActionJourneyNode::class.simpleName -> IssueFreespinActionJourneyNode(
                id = id.value,
                identity = issueFreespinIdentity!!,
                next = nextDomain,
            )

            PlacePayloadActionJourneyNode::class.simpleName -> PlacePayloadActionJourneyNode(
                id = id.value,
                key = placePayloadKey!!,
                value = placePayloadValue!!,
                next = nextDomain,
            )

            else -> error("Unknown journey node type: $type")
        }

        cache[id.value] = node
        return node
    }
}
