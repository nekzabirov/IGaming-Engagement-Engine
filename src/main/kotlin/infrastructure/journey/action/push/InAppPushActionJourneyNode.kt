package com.nekgambling.infrastructure.journey.action.push

import com.nekgambling.domain.model.journey.IJourneyNode

class InAppPushActionJourneyNode(
    id: Long = Long.MIN_VALUE,
    next: IJourneyNode? = null,
    templateId: String,
    placeHolders: Map<String, Any> = mapOf()
) : IPushActionJourneyNode(id, next, templateId, placeHolders)