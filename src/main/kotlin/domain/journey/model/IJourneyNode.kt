package com.nekgambling.domain.journey.model

sealed interface IJourneyNode {
    val prev: IJourneyNode?

    val next: IJourneyNode?
}