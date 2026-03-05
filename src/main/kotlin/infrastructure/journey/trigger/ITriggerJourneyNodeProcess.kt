package com.nekgambling.infrastructure.journey.trigger

import com.nekgambling.domain.journey.strategy.JourneyNodeProcess

interface ITriggerJourneyNodeProcess<T : ITriggerJourneyNode> : JourneyNodeProcess<T>