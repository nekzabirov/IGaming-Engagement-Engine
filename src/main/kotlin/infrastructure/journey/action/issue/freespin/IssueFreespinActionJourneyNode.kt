package com.nekgambling.infrastructure.journey.action.issue.freespin

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.infrastructure.journey.action.IActionJourneyNode
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("issueFreespin")
data class IssueFreespinActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    @Polymorphic override val next: IJourneyNode? = null,
    val freespinIdentity: String,
) : IActionJourneyNode(id, next)
