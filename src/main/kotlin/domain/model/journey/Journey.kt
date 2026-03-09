package com.nekgambling.domain.model.journey

data class Journey(
    val id: Long = Long.MIN_VALUE,
    val identity: String,
    val head: IJourneyNode
) {
    val tail: IJourneyNode
        get() {
            var node = head
            while (node.next != null) {
                node = node.next
            }
            return node
        }
}
