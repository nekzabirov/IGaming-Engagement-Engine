package com.nekgambling.domain.journey.model

interface IJourneyNode {
    val prev: IJourneyNode?

    val next: IJourneyNode?

    fun requireParams(): Set<String>

    fun outputParams(): Set<String>
}