package com.nekgambling.domain.strategy

import kotlinx.serialization.Serializable

@Serializable
enum class NodeCategory { TRIGGER, ACTION, CONDITION, EXTRACTOR }
