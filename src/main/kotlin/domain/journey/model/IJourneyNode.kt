package com.nekgambling.domain.journey.model

interface IJourneyNode {
    val prev: IJourneyNode?

    val next: IJourneyNode?

    fun inputParams(): Set<String>

    fun outputParams(): Set<String>
}