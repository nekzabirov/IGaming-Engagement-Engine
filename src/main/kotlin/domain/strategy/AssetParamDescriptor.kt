package com.nekgambling.domain.strategy

import kotlinx.serialization.Serializable

@Serializable
data class AssetParamDescriptor(
    val name: String,
    val type: ParamType,
    val required: Boolean,
    val description: String? = null,
    val enumValues: List<String>? = null,
    val subtypes: List<SubtypeDescriptor>? = null,
)

@Serializable
data class SubtypeDescriptor(
    val discriminatorValue: String,
    val assets: List<AssetParamDescriptor>,
)

@Serializable
enum class ParamType {
    STRING, LONG, INT, DOUBLE, BOOLEAN, ENUM, CURRENCY, OBJECT, MAP, ANY
}
