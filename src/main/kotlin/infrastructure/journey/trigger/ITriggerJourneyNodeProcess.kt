package com.nekgambling.infrastructure.journey.trigger

import com.nekgambling.domain.strategy.JourneyNodeProcess

interface ITriggerJourneyNodeProcess<T : ITriggerJourneyNode> : JourneyNodeProcess<T>