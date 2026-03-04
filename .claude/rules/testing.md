---
description: Testing rules for crm-engine
globs: ["src/test/**/*.kt"]
---

# Testing Rules

## Test Setup
- All tests extend `AbstractClickHouseTest` which provides a `ClickHouseClient` and truncates tables in `@BeforeTest`
- Tests require a running ClickHouse at `localhost:8123`
- Mock `IEventAdapter` and `ICurrencyAdapter` with MockK — never use real implementations in tests
- Use `coEvery` / `coVerify` for suspend function mocks

## Test Structure
- Use case tests verify: correct ClickHouse persistence, correct event type published, correct event field values
- Query handler tests verify: correct SQL aggregation, date range filtering, player isolation

## Running Tests
```bash
docker-compose up -d clickhouse    # Required dependency
./gradlew test                     # All tests
./gradlew test --tests "com.nekgambling.application.usecase.player.bonus.BonusUseCaseTest"  # Single class
```
