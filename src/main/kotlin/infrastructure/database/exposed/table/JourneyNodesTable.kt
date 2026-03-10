package com.nekgambling.infrastructure.database.exposed.table

import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.database.exposed.mapper.numberParamValueJson
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.jsonb

object JourneyNodesTable : LongIdTable("journey_nodes") {
    val type = varchar("type", 255)

    val journeyId = reference("journey_id", JourneysTable)

    val next = optReference("next_id", JourneyNodesTable)

    // BonusTriggerJourneyNode
    val bonusId = varchar("bonus_id", 255).nullable()
    val bonusIdentity = varchar("bonus_identity", 255).nullable()
    val bonusStatus = varchar("bonus_status", 50).nullable()
    val bonusPayoutAmount = jsonb<NumberParamValue>("bonus_payout_amount", numberParamValueJson).nullable()

    // FreespinTriggerJourneyNode
    val freespinId = varchar("freespin_id", 255).nullable()
    val freespinIdentity = varchar("freespin_identity", 255).nullable()
    val gameId = varchar("game_id", 255).nullable()
    val freespinStatus = varchar("freespin_status", 50).nullable()
    val freespinPayoutRealAmount = jsonb<NumberParamValue>("freespin_payout_real_amount", numberParamValueJson).nullable()

    // InvoiceTriggerJourneyNode
    val invoiceType = varchar("invoice_type", 50).nullable()
    val invoiceStatus = varchar("invoice_status", 50).nullable()
    val invoiceCurrency = varchar("invoice_currency", 10).nullable()
    val invoiceAmount = jsonb<NumberParamValue>("invoice_amount", numberParamValueJson).nullable()

    // SegmentTriggerJourneyNode
    val segmentType = varchar("segment_type", 50).nullable()
    val segmentIdentity = varchar("segment_identity", 255).nullable()

    // IssueFreespinActionJourneyNode
    val issueFreespinIdentity = varchar("issue_freespin_identity", 255).nullable()

    // PlacePayloadActionJourneyNode
    val placePayloadKey = varchar("place_payload_key", 255).nullable()
    val placePayloadValue = varchar("place_payload_value", 1024).nullable()
}
