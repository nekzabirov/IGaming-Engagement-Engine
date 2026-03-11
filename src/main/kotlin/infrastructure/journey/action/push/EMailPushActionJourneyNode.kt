package com.nekgambling.infrastructure.journey.action.push

import com.nekgambling.domain.model.journey.IJourneyNode
import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("emailPush")
data class EMailPushActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    @Polymorphic override val next: IJourneyNode? = null,
    override val templateId: String,
    override val placeHolders: Map<String, @Contextual Any> = mapOf()
) : IPushActionJourneyNode(id, next, templateId, placeHolders)
