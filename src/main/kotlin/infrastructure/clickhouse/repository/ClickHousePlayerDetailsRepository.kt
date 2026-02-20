package com.nekgambling.infrastructure.clickhouse.repository

import com.nekgambling.domain.player.model.PlayerDetails
import com.nekgambling.domain.player.repository.IPlayerDetailsRepository
import com.nekgambling.domain.vo.Country
import com.nekgambling.domain.vo.Locale
import com.nekgambling.infrastructure.clickhouse.ClickHouseClient
import kotlinx.datetime.Instant
import java.sql.Date
import java.sql.ResultSet
import java.util.Optional

class ClickHousePlayerDetailsRepository(
    private val client: ClickHouseClient,
) : IPlayerDetailsRepository {

    override suspend fun save(data: PlayerDetails): PlayerDetails {
        client.execute(
            """
            INSERT INTO player_details (
                id, username, email, phone, email_confirmed, phone_confirmed,
                status, first_name, last_name, middle_name, birth_date,
                country, locale, personal_number, is_verified, gender,
                address, affiliate_tag, registered_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            listOf(
                data.id,
                data.username,
                data.email,
                data.phone,
                data.emailConfirmed,
                data.phoneConfirmed,
                data.status.name,
                data.firstName,
                data.lastName,
                data.middleName,
                data.birthDate?.let { Date(it.time) },
                data.country?.code,
                data.locale?.value,
                data.personalNumber,
                data.isVerified,
                data.gender?.name,
                data.address,
                data.affiliateTag,
                java.time.Instant.ofEpochMilli(data.registeredAt.toEpochMilliseconds()),
            )
        )
        return data
    }

    override suspend fun findById(id: String): Optional<PlayerDetails> {
        val result = client.queryOne(
            "SELECT * FROM player_details FINAL WHERE id = ?",
            listOf(id),
            ::mapRow,
        )
        return Optional.ofNullable(result)
    }

    private fun mapRow(rs: ResultSet): PlayerDetails {
        return PlayerDetails(
            id = rs.getString("id"),
            username = rs.getString("username"),
            email = rs.getString("email"),
            phone = rs.getString("phone"),
            emailConfirmed = rs.getBoolean("email_confirmed"),
            phoneConfirmed = rs.getBoolean("phone_confirmed"),
            status = PlayerDetails.Status.valueOf(rs.getString("status")),
            firstName = rs.getString("first_name"),
            lastName = rs.getString("last_name"),
            middleName = rs.getString("middle_name"),
            birthDate = rs.getDate("birth_date"),
            country = rs.getString("country")?.let { Country(it) },
            locale = rs.getString("locale")?.let { Locale(it) },
            personalNumber = rs.getString("personal_number"),
            isVerified = rs.getBoolean("is_verified"),
            gender = rs.getString("gender")?.let { PlayerDetails.Gender.valueOf(it) },
            address = rs.getString("address"),
            affiliateTag = rs.getString("affiliate_tag"),
            registeredAt = Instant.fromEpochMilliseconds(rs.getTimestamp("registered_at").time),
        )
    }
}
