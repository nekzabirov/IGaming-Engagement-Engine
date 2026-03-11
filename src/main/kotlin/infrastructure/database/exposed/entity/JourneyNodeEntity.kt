package com.nekgambling.infrastructure.database.exposed.entity

import com.nekgambling.infrastructure.database.exposed.table.JourneyNodesTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class JourneyNodeEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<JourneyNodeEntity>(JourneyNodesTable)

    var type by JourneyNodesTable.type
    var journey by JourneyEntity referencedOn JourneyNodesTable.journeyId
    var next by JourneyNodeEntity optionalReferencedOn JourneyNodesTable.next

    // --- Discriminator ---
    var subType by JourneyNodesTable.subType

    // --- Condition nodes ---
    var notMatchNode by JourneyNodeEntity optionalReferencedOn JourneyNodesTable.notMatchNode
    var inputKey by JourneyNodesTable.inputKey
    var expected by JourneyNodesTable.expected
    var threshold by JourneyNodesTable.threshold
    var targetNumber by JourneyNodesTable.targetNumber
    var targetString by JourneyNodesTable.targetString
    var rangeMin by JourneyNodesTable.rangeMin
    var rangeMax by JourneyNodesTable.rangeMax

    // --- Trigger: bonus ---
    var bonusId by JourneyNodesTable.bonusId
    var bonusIdentity by JourneyNodesTable.bonusIdentity
    var bonusStatus by JourneyNodesTable.bonusStatus
    var bonusPayoutAmount by JourneyNodesTable.bonusPayoutAmount

    // --- Trigger: freespin ---
    var freespinId by JourneyNodesTable.freespinId
    var freespinIdentity by JourneyNodesTable.freespinIdentity
    var gameId by JourneyNodesTable.gameId
    var freespinStatus by JourneyNodesTable.freespinStatus
    var freespinPayoutRealAmount by JourneyNodesTable.freespinPayoutRealAmount

    // --- Trigger: invoice ---
    var invoiceType by JourneyNodesTable.invoiceType
    var invoiceStatus by JourneyNodesTable.invoiceStatus
    var invoiceAmount by JourneyNodesTable.invoiceAmount

    // --- Trigger: segment ---
    var segmentType by JourneyNodesTable.segmentType
    var segment by JourneyNodesTable.segment

    // --- Shared ---
    var currency by JourneyNodesTable.currency

    // --- Action: push ---
    var templateId by JourneyNodesTable.templateId
    var placeHolders by JourneyNodesTable.placeHolders

    // --- Action: identity ---
    var nodeIdentity by JourneyNodesTable.nodeIdentity

    // --- Action: fixed bonus ---
    var amount by JourneyNodesTable.amount

    // --- Action: payload ---
    var payloadKey by JourneyNodesTable.payloadKey
    var payloadValue by JourneyNodesTable.payloadValue

    // --- Extractor: date ---
    var dateParam by JourneyNodesTable.dateParam

    // --- Extractor: percentage ---
    var percentage by JourneyNodesTable.percentage
    var maxAmount by JourneyNodesTable.maxAmount
}
