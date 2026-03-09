package com.nekgambling.infrastructure.journey.action

import com.nekgambling.domain.strategy.JourneyNodeProcess

abstract class IActionJourneyNodeProcess<T : IActionJourneyNode> : JourneyNodeProcess<T> {
}