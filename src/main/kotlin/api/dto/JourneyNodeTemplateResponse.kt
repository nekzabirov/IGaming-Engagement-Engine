package com.nekgambling.api.dto

import com.nekgambling.domain.strategy.AssetParamDescriptor
import com.nekgambling.domain.strategy.NodeCategory
import kotlinx.serialization.Serializable

@Serializable
data class JourneyNodeTemplateResponse(
    val identity: String,
    val category: NodeCategory,
    val inputParams: Set<String>,
    val outputParams: Set<String>,
    val assets: List<AssetParamDescriptor>,
)
