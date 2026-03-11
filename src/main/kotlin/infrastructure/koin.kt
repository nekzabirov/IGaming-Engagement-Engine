package com.nekgambling.infrastructure

import com.nekgambling.api.command.ProcessInvoiceCommandHandler
import com.nekgambling.api.command.ProcessPlayerCommandHandler
import com.nekgambling.api.command.ProcessSpinCommandHandler
import com.nekgambling.application.adapter.ICurrencyAdapter
import com.nekgambling.application.adapter.IEventAdapter
import com.nekgambling.application.adapter.ILockAdapter
import com.nekgambling.application.cqrs.command.CommandBus
import com.nekgambling.application.cqrs.command.ICommandHandler
import com.nekgambling.application.resolver.JourneyNodeProcessResolver
import com.nekgambling.application.cqrs.query.IQueryHandler
import com.nekgambling.application.cqrs.query.QueryBus
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
import com.nekgambling.infrastructure.journey.extractor.amount.AmountExtractorProcess
import com.nekgambling.infrastructure.journey.extractor.amount.PercentageAmountExtractorParams
import com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal.InvoiceTotalExtractorNomenclature
import com.nekgambling.infrastructure.journey.extractor.player.invoiceTotal.InvoiceTotalExtractorProcess
import com.nekgambling.infrastructure.journey.extractor.player.playerAge.PlayerAgeExtractorNomenclature
import com.nekgambling.infrastructure.journey.extractor.player.playerAge.PlayerAgeExtractorProcess
import com.nekgambling.infrastructure.journey.extractor.player.playerGgr.PlayerGgrExtractorNomenclature
import com.nekgambling.infrastructure.journey.extractor.player.playerGgr.PlayerGgrExtractorProcess
import com.nekgambling.infrastructure.journey.extractor.player.playerProfile.PlayerProfileExtractorNomenclature
import com.nekgambling.infrastructure.journey.extractor.player.playerProfile.PlayerProfileExtractorProcess
import com.nekgambling.infrastructure.journey.extractor.player.spinTotal.SpinTotalExtractorNomenclature
import com.nekgambling.infrastructure.journey.extractor.player.spinTotal.SpinTotalExtractorProcess
import com.nekgambling.infrastructure.journey.trigger.bonus.BonusTriggerJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.trigger.freespin.FreespinTriggerJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.trigger.invoice.InvoiceTriggerJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueDynamicBonusActionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueFixedBonusActionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.issue.freespin.IssueFreespinActionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.issue.bonus.IssueBonusActionJourneyNodeProcess
import com.nekgambling.infrastructure.journey.action.issue.freespin.IssueFreespinIActionJourneyNodeProcess
import com.nekgambling.infrastructure.journey.action.payload.PlacePayloadActionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.payload.PlacePayloadActionJourneyNodeProcess
import com.nekgambling.infrastructure.journey.action.push.PushActionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.action.push.PushIActionJourneyNodeProcess
import com.nekgambling.infrastructure.journey.condition.ConditionJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.condition.ConditionJourneyNodeProcess
import com.nekgambling.infrastructure.journey.trigger.bonus.BonusTriggerJourneyNodeProcess
import com.nekgambling.infrastructure.journey.trigger.freespin.FreespinTriggerJourneyNodeProcess
import com.nekgambling.infrastructure.journey.trigger.invoice.InvoiceTriggerJourneyNodeProcess
import com.nekgambling.infrastructure.journey.trigger.segment.SegmentTriggerJourneyNodeNomenclature
import com.nekgambling.infrastructure.journey.trigger.segment.SegmentTriggerJourneyNodeProcess
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

    // --- Journey ---

    journeyModule()
}

private fun org.koin.core.module.Module.journeyModule() {

    // --- Journey node nomenclatures ---

    single<JourneyNodeNomenclature<*>> { BonusTriggerJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { FreespinTriggerJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { InvoiceTriggerJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { SegmentTriggerJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { PercentageAmountExtractorParams } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { PlayerProfileExtractorNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { PlayerAgeExtractorNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { SpinTotalExtractorNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { InvoiceTotalExtractorNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { PlayerGgrExtractorNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { PushActionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { IssueFixedBonusActionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { IssueDynamicBonusActionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { IssueFreespinActionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { PlacePayloadActionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    single<JourneyNodeNomenclature<*>> { ConditionJourneyNodeNomenclature } bind JourneyNodeNomenclature::class

    // --- Journey node processors ---

    single { BonusTriggerJourneyNodeProcess() } bind JourneyNodeProcess::class

    single { FreespinTriggerJourneyNodeProcess() } bind JourneyNodeProcess::class

    single { InvoiceTriggerJourneyNodeProcess() } bind JourneyNodeProcess::class

    single { SegmentTriggerJourneyNodeProcess() } bind JourneyNodeProcess::class

    single { AmountExtractorProcess() } bind JourneyNodeProcess::class

    single { PlayerProfileExtractorProcess(get()) } bind JourneyNodeProcess::class

    single { PlayerAgeExtractorProcess(get()) } bind JourneyNodeProcess::class

    single { SpinTotalExtractorProcess(get()) } bind JourneyNodeProcess::class

    single { InvoiceTotalExtractorProcess(get()) } bind JourneyNodeProcess::class

    single { PlayerGgrExtractorProcess(get()) } bind JourneyNodeProcess::class

    single { PushIActionJourneyNodeProcess() } bind JourneyNodeProcess::class

    single { IssueBonusActionJourneyNodeProcess() } bind JourneyNodeProcess::class

    single { IssueFreespinIActionJourneyNodeProcess() } bind JourneyNodeProcess::class

    single { PlacePayloadActionJourneyNodeProcess() } bind JourneyNodeProcess::class

    single { ConditionJourneyNodeProcess() } bind JourneyNodeProcess::class

    single { JourneyNodeProcessResolver(getAll()) }

}
