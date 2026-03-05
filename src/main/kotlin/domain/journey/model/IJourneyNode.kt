package com.nekgambling.domain.journey.model

abstract class IJourneyNode(
    val prev: IJourneyNode? = null,
    val next: IJourneyNode? = null,
) {

    init {
        var node = next
        while (node != null) {
            require(node !== this) { "Circular dependency detected in journey node chain (next)" }
            node = node.next
        }
        node = prev
        while (node != null) {
            require(node !== this) { "Circular dependency detected in journey node chain (prev)" }
            node = node.prev
        }
    }

    abstract fun inputParams(): Set<String>

    abstract fun outputParams(): Set<String>
}
