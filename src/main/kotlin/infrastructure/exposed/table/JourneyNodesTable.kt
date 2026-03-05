package com.nekgambling.infrastructure.exposed.table

import com.nekgambling.domain.vo.param.NumberParamValue
import com.nekgambling.infrastructure.exposed.numberParamValueJson
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.json.jsonb

object JourneyNodesTable : LongIdTable("journey_nodes") {
    val type = varchar("type", 255)

    val journeyId = reference("journey_id", JourneysTable)

    val next = optReference("next_id", JourneyNodesTable)

    // ConditionJourneyNode
    val conditionId = optReference("condition_id", ConditionsTable)
    val onMismatch = optReference("on_mismatch_id", JourneyNodesTable)

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

    // SegmentEnterTriggerJourneyNode / SegmentExitTriggerJourneyNode
    val segmentIdentity = varchar("segment_identity", 255).nullable()
}
