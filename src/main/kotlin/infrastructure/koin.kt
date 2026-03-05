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
import com.nekgambling.application.usecase.player.bonus.IssueBonusUseCase
import com.nekgambling.application.usecase.player.bonus.LostBonusUseCase
import com.nekgambling.application.usecase.player.bonus.StartWageringBonusUseCase
import com.nekgambling.application.usecase.player.bonus.WageredBonusUseCase
import com.nekgambling.application.usecase.player.freespin.ActivateFreespinUseCase
import com.nekgambling.application.usecase.player.freespin.CancelFreespinUseCase
import com.nekgambling.application.usecase.player.freespin.FinishFreespinUseCase
import com.nekgambling.application.usecase.player.freespin.IssueFreespinUseCase
import com.nekgambling.application.usecase.player.invoice.CreateInvoiceUseCase
import com.nekgambling.application.usecase.player.invoice.UpdateInvoiceUseCase
import com.nekgambling.application.usecase.player.player.UpdatePlayerUseCase
import com.nekgambling.application.usecase.player.spin.PlaceSpinUseCase
import com.nekgambling.application.usecase.player.spin.SettleSpinUseCase
import com.nekgambling.application.usecase.segment.ProcessConditionUsecase
import com.nekgambling.application.usecase.segment.ProcessSegmentUsecase
import com.nekgambling.domain.condition.strategy.ConditionRuleEvaluatorResolver
import com.nekgambling.domain.condition.strategy.IConditionRuleEvaluator
import com.nekgambling.domain.player.repository.IPlayerBonusRepository
import com.nekgambling.domain.player.repository.IPlayerDetailsRepository
import com.nekgambling.domain.player.repository.IPlayerFreespinRepository
import com.nekgambling.domain.player.repository.IPlayerInvoiceRepository
import com.nekgambling.domain.player.repository.IPlayerSpinRepository
import com.nekgambling.domain.condition.repository.IConditionRepository
import com.nekgambling.domain.journey.repository.IJourneyRepository
import com.nekgambling.domain.segment.repository.ISegmentRepository
import com.nekgambling.infrastructure.currency.UnitsCurrencyAdapter
import com.nekgambling.infrastructure.exposed.ExposedConfig
import com.nekgambling.infrastructure.exposed.ExposedDatabaseFactory
import com.nekgambling.infrastructure.exposed.repository.ExposedConditionRepository
import com.nekgambling.infrastructure.exposed.repository.ExposedJourneyRepository
import com.nekgambling.infrastructure.exposed.repository.ExposedSegmentRepository
import com.nekgambling.infrastructure.clickhouse.ClickHouseClient
import com.nekgambling.infrastructure.clickhouse.config.ClickHouseConfig
import com.nekgambling.infrastructure.clickhouse.query.ClickHousePlayerBonusPayoutTotalQueryHandler
import com.nekgambling.infrastructure.clickhouse.query.ClickHousePlayerInvoiceTotalQueryHandler
import com.nekgambling.infrastructure.clickhouse.query.ClickHousePlayerSpinTotalQueryHandler
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerBonusRepository
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerDetailsRepository
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerFreespinRepository
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerInvoiceRepository
import com.nekgambling.infrastructure.clickhouse.repository.ClickHousePlayerSpinRepository
import com.nekgambling.infrastructure.condition.invoiceTotal.InvoiceTotalConditionRuleEvaluator
import com.nekgambling.infrastructure.condition.playerAge.PlayerAgeConditionRuleEvaluator
import com.nekgambling.infrastructure.condition.playerGGR.PlayerGgrConditionRuleEvaluator
import com.nekgambling.infrastructure.condition.profile.ProfileFieldConditionRuleEvaluator
import com.nekgambling.infrastructure.condition.spinTotal.SpinTotalConditionRuleEvaluator
import com.nekgambling.infrastructure.rabbitmq.RabbitMQEventAdapter
import com.nekgambling.infrastructure.rabbitmq.config.RabbitMQConfig
import com.nekgambling.infrastructure.redis.RedisLockAdapter
import com.nekgambling.infrastructure.redis.config.RedisConfig
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

    single { ExposedConditionRepository(get()) } bind IConditionRepository::class

    single { ExposedSegmentRepository(get()) } bind ISegmentRepository::class

    single { ExposedJourneyRepository(get()) } bind IJourneyRepository::class

    // --- Query handlers ---

    single { ClickHousePlayerBonusPayoutTotalQueryHandler(get()) } bind IQueryHandler::class

    single { ClickHousePlayerInvoiceTotalQueryHandler(get()) } bind IQueryHandler::class

    single { ClickHousePlayerSpinTotalQueryHandler(get()) } bind IQueryHandler::class

    single { QueryBus(getAll()) }

    // --- Command handlers ---

    single { ProcessPlayerCommandHandler(get()) } bind ICommandHandler::class

    single { ProcessInvoiceCommandHandler(get(), get(), get(), get()) } bind ICommandHandler::class

    single { ProcessSpinCommandHandler(get(), get(), get(), get()) } bind ICommandHandler::class

    single { CommandBus(getAll()) }

    // --- Condition rule evaluators ---

    single { PlayerAgeConditionRuleEvaluator(get()) } bind IConditionRuleEvaluator::class

    single { ProfileFieldConditionRuleEvaluator(get()) } bind IConditionRuleEvaluator::class

    single { SpinTotalConditionRuleEvaluator(get()) } bind IConditionRuleEvaluator::class

    single { InvoiceTotalConditionRuleEvaluator(get()) } bind IConditionRuleEvaluator::class

    single { PlayerGgrConditionRuleEvaluator(get()) } bind IConditionRuleEvaluator::class

    single { ConditionRuleEvaluatorResolver(getAll()) }

    // --- Use cases ---

    factory { IssueBonusUseCase(get(), get(), get()) }

    factory { LostBonusUseCase(get(), get()) }

    factory { StartWageringBonusUseCase(get(), get()) }

    factory { WageredBonusUseCase(get(), get(), get()) }

    factory { IssueFreespinUseCase(get(), get()) }

    factory { ActivateFreespinUseCase(get(), get()) }

    factory { CancelFreespinUseCase(get(), get()) }

    factory { FinishFreespinUseCase(get(), get(), get()) }

    factory { CreateInvoiceUseCase(get(), get(), get()) }

    factory { UpdateInvoiceUseCase(get(), get(), get()) }

    factory { PlaceSpinUseCase(get(), get(), get()) }

    factory { SettleSpinUseCase(get(), get(), get()) }

    factory { UpdatePlayerUseCase(get(), get()) }

    factory { ProcessConditionUsecase(get(), get(), get(), get()) }

    factory { ProcessSegmentUsecase(get(), get(), get(), get()) }
}
