package com.nekgambling.domain.strategy

fun numberParamValueSubtypes(): List<SubtypeDescriptor> = listOf(
    SubtypeDescriptor(
        discriminatorValue = "greaterThan",
        assets = listOf(AssetParamDescriptor(name = "threshold", type = ParamType.DOUBLE, required = true)),
    ),
    SubtypeDescriptor(
        discriminatorValue = "lessThan",
        assets = listOf(AssetParamDescriptor(name = "threshold", type = ParamType.DOUBLE, required = true)),
    ),
    SubtypeDescriptor(
        discriminatorValue = "inRange",
        assets = listOf(
            AssetParamDescriptor(name = "min", type = ParamType.DOUBLE, required = true),
            AssetParamDescriptor(name = "max", type = ParamType.DOUBLE, required = true),
        ),
    ),
    SubtypeDescriptor(
        discriminatorValue = "equalTo",
        assets = listOf(AssetParamDescriptor(name = "target", type = ParamType.DOUBLE, required = true)),
    ),
)

fun dateParamValueSubtypes(): List<SubtypeDescriptor> = listOf(
    SubtypeDescriptor(
        discriminatorValue = "lastDays",
        assets = listOf(AssetParamDescriptor(name = "days", type = ParamType.INT, required = true)),
    ),
    SubtypeDescriptor(
        discriminatorValue = "range",
        assets = listOf(
            AssetParamDescriptor(name = "from", type = ParamType.STRING, required = true, description = "ISO 8601 instant"),
            AssetParamDescriptor(name = "to", type = ParamType.STRING, required = true, description = "ISO 8601 instant"),
        ),
    ),
)
