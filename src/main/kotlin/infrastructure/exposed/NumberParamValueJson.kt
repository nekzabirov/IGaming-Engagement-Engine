package com.nekgambling.infrastructure.exposed

import com.nekgambling.domain.vo.param.NumberMoreParamValue
import com.nekgambling.domain.vo.param.NumberParamValue
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val numberParamValueSerializersModule = SerializersModule {
    polymorphic(NumberParamValue::class) {
        subclass(NumberMoreParamValue::class)
    }
}

val numberParamValueJson = Json {
    serializersModule = numberParamValueSerializersModule
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}
