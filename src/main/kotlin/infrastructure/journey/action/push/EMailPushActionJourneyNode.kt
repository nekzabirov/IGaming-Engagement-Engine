package com.nekgambling.infrastructure.journey.action.push

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.Locale

data class EMailPushActionJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    override val next: IJourneyNode? = null,
    override val locale: Locale,
    override val templateId: String,
    override val placeHolders: Map<String, Any> = mapOf()
) : IPushActionJourneyNode(id, next, locale, templateId, placeHolders)
