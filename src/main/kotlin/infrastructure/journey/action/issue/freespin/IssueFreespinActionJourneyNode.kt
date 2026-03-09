package com.nekgambling.infrastructure.journey.action.issue.freespin

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.action.IActionJourneyNode

data class IssueFreespinActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    val identity: String,
) : IActionJourneyNode(id, next)
