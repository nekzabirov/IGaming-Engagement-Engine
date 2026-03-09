package com.nekgambling.infrastructure.journey.action.push

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.Locale

class SmsPushActionJourneyNode(
    id: Long = Long.MIN_VALUE,
    next: IJourneyNode? = null,
    locale: Locale,
    templateId: String,
    placeHolders: Map<String, Any> = mapOf()
) : IPushActionJourneyNode(id, next, locale, templateId, placeHolders)