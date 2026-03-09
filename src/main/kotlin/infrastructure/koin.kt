package com.nekgambling.infrastructure

import com.nekgambling.api.command.ProcessInvoiceCommandHandler
import com.nekgambling.api.command.ProcessPlayerCommandHandler
import com.nekgambling.api.command.ProcessSpinCommandHandler
import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.adapter.ILockAdapter
import com.nekgambling.api.command.CommandBus
import com.nekgambling.api.command.ICommandHandler
import com.nekgambling.application.query.IQueryHandler
import com.nekgambling.application.query.QueryBus
import com.nekgambling.domain.repository.IJourneyInstantRepository
import com.nekgambling.domain.repository.IJourneyRepository
import com.nekgambling.domain.repository.player.IPlayerBonusRepository
import com.nekgambling.domain.repository.player.IPlayerDetailsRepository
import com.nekgambling.domain.repository.player.IPlayerFreespinRepository
import com.nekgambling.domain.repository.player.IPlayerInvoiceRepository
import com.nekgambling.domain.repository.player.IPlayerSpinRepository
import com.nekgambling.infrastructure.database.clickhouse.ClickHouseClient
import com.nekgambling.infrastructure.database.clickhouse.config.ClickHouseConfig
import com.nekgambling.infrastructure.database.clickhouse.query.ClickHousePlayerBonusPayoutTotalQueryHandler
import com.nekgambling.infrastructure.database.clickhouse.query.ClickHousePlayerInvoiceTotalQueryHandler
import com.nekgambling.infrastructure.database.clickhouse.query.ClickHousePlayerSpinTotalQueryHandler
import com.nekgambling.infrastructure.database.clickhouse.repository.ClickHousePlayerBonusRepository
import com.nekgambling.infrastructure.database.clickhouse.repository.ClickHousePlayerDetailsRepository
import com.nekgambling.infrastructure.database.clickhouse.repository.ClickHousePlayerFreespinRepository
import com.nekgambling.infrastructure.database.clickhouse.repository.ClickHousePlayerInvoiceRepository
import com.nekgambling.infrastructure.database.clickhouse.repository.ClickHousePlayerSpinRepository
import com.nekgambling.infrastructure.database.exposed.ExposedConfig
import com.nekgambling.infrastructure.database.exposed.ExposedDatabaseFactory
import com.nekgambling.infrastructure.database.exposed.repository.ExposedJourneyInstantRepository
import com.nekgambling.infrastructure.database.exposed.repository.ExposedJourneyRepository
import com.nekgambling.infrastructure.external.currency.UnitsCurrencyAdapter
import com.nekgambling.infrastructure.external.rabbitmq.RabbitMQEventAdapter
import com.nekgambling.infrastructure.external.rabbitmq.config.RabbitMQConfig
import com.nekgambling.infrastructure.external.redis.RedisLockAdapter
import com.nekgambling.infrastructure.external.redis.config.RedisConfig
import com.nekgambling.domain.strategy.JourneyNodeNomenclature
import com.nekgambling.domain.strategy.JourneyNodeProcess
import com.nekgambling.infrastructure.journey.extractor.amount.PercentageAmountExtractorParams
import com.nekgambling.infrastructure.journey.extractor.playerProfile.PlayerProfileExtractorParams
import com.nekgambling.infrastructure.journey.player.IPlayerDefinitionEvaluator
import com.nekgambling.infrastructure.journey.player.PlayerJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.trigger.bonus.BonusTriggerJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.trigger.freespin.FreespinTriggerJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.trigger.invoice.InvoiceTriggerJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueDynamicBonusActionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueFixedBonusActionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.issue.freespin.IssueFreespinActionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.issue.freespin.IssueFreespinIActionJourneyNodeProcess
import com.nekgambling.infrastructure.journey.action.payload.PlacePayloadActionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.payload.PlacePayloadActionJourneyNodeProcess
import com.nekgambling.infrastructure.journey.action.push.PushActionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.trigger.segment.SegmentTriggerJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.player.invoiceTotal.InvoiceTotalPlayerDefinitionEvaluator
import com.nekgambling.infrastructure.journey.player.playerAge.PlayerAgeDefinitionEvaluator
import com.nekgambling.infrastructure.journey.player.playerGGR.PlayerGgrPlayerEvaluator
import com.nekgambling.infrastructure.journey.player.profile.ProfileFieldPlayerDefinitionEvaluator
import com.nekgambling.infrastructure.journey.player.spinTotal.SpinTotalPlayerDefinitionEvaluator
import org.koin.dsl.bind
import org.koin.dsl.module

private fun env(name: String): String =
    System.getenv(name) ?: error("Missing required environment variable: $name")

private fun env(name: String, default: String): String =
    System.getenv(name) ?: default

val infrastructureModule = module {

    // --- Configs ---

    single {
        ClickHouseConfig(
            url = env("CLICKHOUSE_URL"),
            username = env("CLICKHOUSE_USERNAME", "default"),
            password = env("CLICKHOUSE_PASSWORD", ""),
        )
    }

    single {
        RabbitMQConfig(
            host = env("RABBITMQ_HOST"),
            port = env("RABBITMQ_PORT", "5672").toInt(),
            username = env("RABBITMQ_USERNAME"),
            password = env("RABBITMQ_PASSWORD"),
            exchange = env("RABBITMQ_EXCHANGE", "crm.events"),
            exchangeType = env("RABBITMQ_EXCHANGE_TYPE", "topic"),
        )
    }

    single {
        RedisConfig(
            host = env("REDIS_HOST"),
            port = env("REDIS_PORT", "6379").toInt(),
            password = System.getenv("REDIS_PASSWORD"),
        )
    }

    single {
        ExposedConfig(
            url = env("DB_URL"),
            username = env("DB_USERNAME"),
            password = env("DB_PASSWORD"),
        )
    }

    // --- Infrastructure clients ---

    single { ExposedDatabaseFactory.init(get<ExposedConfig>()) }

    single {
        ClickHouseClient(get()).apply { initTables() }
    }

    single { RabbitMQEventAdapter(get()) } bind IEventAdapter::class

    single { RedisLockAdapter(get()) } bind ILockAdapter::class

    single { UnitsCurrencyAdapter() } bind ICurrencyAdapter::class

    // --- Repositories ---

    single { ClickHousePlayerBonusRepository(get()) } bind IPlayerBonusRepository::class

    single { ClickHousePlayerDetailsRepository(get()) } bind IPlayerDetailsRepository::class

    single { ClickHousePlayerFreespinRepository(get()) } bind IPlayerFreespinRepository::class

    single { ClickHousePlayerInvoiceRepository(get()) } bind IPlayerInvoiceRepository::class

    single { ClickHousePlayerSpinRepository(get()) } bind IPlayerSpinRepository::class

    single { ExposedJourneyRepository(get()) } bind IJourneyRepository::class

    single { ExposedJourneyInstantRepository(get()) } bind IJourneyInstantRepository::class

    // --- Query handlers ---

    single { ClickHousePlayerBonusPayoutTotalQueryHandler(get()) } bind IQueryHandler::class

    single { ClickHousePlayerInvoiceTotalQueryHandler(get()) } bind IQueryHandler::class

    single { ClickHousePlayerSpinTotalQueryHandler(get()) } bind IQueryHandler::class

    single { QueryBus(getAll()) }

    // --- Command handlers ---

    single { ProcessPlayerCommandHandler(get(), get()) } bind ICommandHandler::class

    single { ProcessInvoiceCommandHandler(get(), get(), get()) } bind ICommandHandler::class

    single { ProcessSpinCommandHandler(get(), get(), get()) } bind ICommandHandler::class

    single { CommandBus(getAll()) }

    // --- Journey node params ---

    single<JourneyNodeNomenclature<*>> { BonusTriggerJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { FreespinTriggerJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { InvoiceTriggerJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { SegmentTriggerJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { PercentageAmountExtractorParams } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { PlayerProfileExtractorParams } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { PlayerJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { PushActionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { IssueFixedBonusActionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { IssueDynamicBonusActionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { IssueFreespinActionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single { IssueFreespinIActionJourneyNodeProcess() } bind JourneyNodeProcess::class

    single<JourneyNodeNomenclature<*>> { PlacePayloadActionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single { PlacePayloadActionJourneyNodeProcess() } bind JourneyNodeProcess::class

    // --- Player definition evaluators ---

    single { PlayerAgeDefinitionEvaluator(get()) } bind IPlayerDefinitionEvaluator::class

    single { ProfileFieldPlayerDefinitionEvaluator(get()) } bind IPlayerDefinitionEvaluator::class

    single { SpinTotalPlayerDefinitionEvaluator(get()) } bind IPlayerDefinitionEvaluator::class

    single { InvoiceTotalPlayerDefinitionEvaluator(get()) } bind IPlayerDefinitionEvaluator::class

    single { PlayerGgrPlayerEvaluator(get()) } bind IPlayerDefinitionEvaluator::class
}
