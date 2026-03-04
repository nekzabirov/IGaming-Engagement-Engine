package com.nekgambling.infrastructure.condition.profile

import com.nekgambling.domain.condition.model.IConditionRule
import com.nekgambling.domain.vo.param.ParamValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("profileField")
data class ProfileFieldConditionRule(
    val field: Field,

    val value: ParamValue
) : IConditionRule {
    enum class Field {
        USERNAME,
        EMAIL,
        PHONE,
        EMAIL_CONFIRMED,
        PHONE_CONFIRMED,
        STATUS,
        FIRST_NAME,
        LAST_NAME,
        MIDDLE_NAME,
        BIRTH_DATE,
        COUNTRY,
        LOCALE,
        PERSONAL_NUMBER,
        IS_VERIFIED,
        GENDER,
        ADDRESS,
        AFFILIATE_TAG,
        REGISTERED_AT
    }
}
