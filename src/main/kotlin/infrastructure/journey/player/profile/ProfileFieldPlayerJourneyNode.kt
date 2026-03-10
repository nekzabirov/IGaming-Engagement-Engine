package com.nekgambling.infrastructure.journey.player.profile

import com.nekgambling.domain.model.journey.IJourneyNode
import com.nekgambling.domain.vo.param.ParamValue
import com.nekgambling.infrastructure.journey.player.PlayerJourneyNode

data class ProfileFieldPlayerJourneyNode(
    override val id: Long = Long.MIN_VALUE,
    val field: Field,
    val expectedValue: ParamValue,
    override val matchNode: IJourneyNode? = null,
    override val notMatchNode: IJourneyNode? = null,
) : PlayerJourneyNode(id = id, matchNode = matchNode, notMatchNode = notMatchNode) {
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
