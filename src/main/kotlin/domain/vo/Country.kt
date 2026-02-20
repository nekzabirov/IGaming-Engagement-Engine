package com.nekgambling.domain.vo

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Country(val code: String) {
    init {
        require(code.length == 2) { "Country code must be 2 characters (ISO 3166-1 alpha-2)" }
        require(code.all { it.isUpperCase() }) { "Country code must be uppercase" }
    }

    override fun toString(): String = code
}