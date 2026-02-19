package com.nekgambling.segment.condition.profile

import com.nekgambling.segment.condition.ICondition
import com.nekgambling.segment.param.IParamValue

data class ProfileFieldCondition(
    val field: Field,

    val value: IParamValue
) : ICondition {
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
