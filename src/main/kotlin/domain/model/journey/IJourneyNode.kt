package com.nekgambling.domain.model.journey

abstract class IJourneyNode(
    open val id: Long = Long.MIN_VALUE,
    open val next: IJourneyNode? = null,
) {

    init {
        var node = next
        while (node != null) {
            require(node !== this) { "Circular dependency detected in journey node chain (next)" }
            node = node.next
        }
    }

    abstract fun inputParams(): Set<String>

    abstract fun outputParams(): Set<String>
}
