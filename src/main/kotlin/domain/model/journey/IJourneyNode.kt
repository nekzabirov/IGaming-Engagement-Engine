package com.nekgambling.domain.model.journey

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class IJourneyNode(
    @Transient open val id: Long = Long.MIN_VALUE,
    @Transient open val next: IJourneyNode? = null,
) {

    init {
        var node = next
        while (node != null) {
            require(node !== this) { "Circular dependency detected in journey node chain (next)" }
            node = node.next
        }
    }

}
