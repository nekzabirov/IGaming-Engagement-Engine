package com.nekgambling.infrastructure.journey.player

import com.nekgambling.domain.strategy.JourneyNodeProcess

interface IPlayerJourneyNodeProcess<T : PlayerJourneyNode> : JourneyNodeProcess<T>
