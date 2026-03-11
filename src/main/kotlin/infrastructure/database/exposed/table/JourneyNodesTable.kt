package com.nekgambling.infrastructure.database.exposed.table

import org.jetbrains.exposed.dao.id.LongIdTable

object JourneyNodesTable : LongIdTable("journey_nodes") {
    val type = varchar("type", 255)
    val journeyId = reference("journey_id", JourneysTable)
    val next = optReference("next_id", JourneyNodesTable)

    // --- Discriminator (condition subtype, push channel, etc.) ---
    val subType = varchar("sub_type", 255).nullable()

    // --- Condition nodes ---
    val notMatchNode = optReference("not_match_node_id", JourneyNodesTable)
    val inputKey = varchar("input_key", 255).nullable()
    val expected = bool("expected").nullable()
    val threshold = double("threshold").nullable()
    val targetNumber = double("target_number").nullable()
    val targetString = varchar("target_string", 1024).nullable()
    val rangeMin = double("range_min").nullable()
    val rangeMax = double("range_max").nullable()

    // --- Trigger: bonus ---
    val bonusId = varchar("bonus_id", 255).nullable()
    val bonusIdentity = varchar("bonus_identity", 255).nullable()
    val bonusStatus = varchar("bonus_status", 50).nullable()
    val bonusPayoutAmount = text("bonus_payout_amount").nullable()

    // --- Trigger: freespin ---
    val freespinId = varchar("freespin_id", 255).nullable()
    val freespinIdentity = varchar("freespin_identity", 255).nullable()
    val gameId = varchar("game_id", 255).nullable()
    val freespinStatus = varchar("freespin_status", 50).nullable()
    val freespinPayoutRealAmount = text("freespin_payout_real_amount").nullable()

    // --- Trigger: invoice ---
    val invoiceType = varchar("invoice_type", 50).nullable()
    val invoiceStatus = varchar("invoice_status", 50).nullable()
    val invoiceAmount = text("invoice_amount").nullable()

    // --- Trigger: segment ---
    val segmentType = varchar("segment_type", 50).nullable()
    val segment = varchar("segment", 255).nullable()

    // --- Shared: currency ---
    val currency = varchar("currency", 10).nullable()

    // --- Action: push ---
    val templateId = varchar("template_id", 255).nullable()
    val placeHolders = text("place_holders").nullable()

    // --- Action: identity (bonus/freespin issue) ---
    val nodeIdentity = varchar("node_identity", 255).nullable()

    // --- Action: fixed bonus amount ---
    val amount = long("amount").nullable()

    // --- Action: payload ---
    val payloadKey = varchar("payload_key", 255).nullable()
    val payloadValue = text("payload_value").nullable()

    // --- Extractor: date-based ---
    val dateParam = text("date_param").nullable()

    // --- Extractor: percentage amount ---
    val percentage = integer("percentage").nullable()
    val maxAmount = long("max_amount").nullable()
}
