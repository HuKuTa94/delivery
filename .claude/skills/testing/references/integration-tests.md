# Integration tests

- Suffix **`*IT`**; they need **Docker** (Testcontainers: `postgres:15.3`, `apache/kafka:3.8.0`;
  Ryuk disabled, containers stopped via shutdown hook). They fail, not skip, without Docker.
- Postgres fixture: `PostgresTestContainer` in `infrastructure/persistence/orm/commons` testFixtures
  (singleton, schema from Liquibase, `cleanData` before each test).
- Base specs: `KtormIntegrationSpec`, `SpringJpaIntegrationSpec`.
- Style rules from [unit-tests.md](unit-tests.md) apply.

## Concurrency of an adapter

A concurrency guarantee of one adapter (deduplication, unique constraints, locking) is tested in that adapter's
`*IT`, not through the use case in front of it. Example: `KtormInboxEventRepositoryIT`,
`SpringJpaInboxEventRepositoryAdapterIT`.

- `StartGate.run(threads, timeout) { thread -> … }` (`persistence/orm/commons` testFixtures, package `integration.concurrency`):
  releases N platform threads at once and returns a `Result` per thread; a hang fails with an `AssertionError`.
  Assert on `outcomes.failuresByType()` so a failure shows which exception and how often.
- Races depend on timing: run many rounds, one fresh piece of test data per round, and check once at the end.
- Keep the threads below the connection pool (Hikari default 10 in `SpringJpaIntegrationSpec`), so they race in
  the database instead of waiting for a connection.
- No tags: these are ordinary tests of `make test`.

Run: `make test` (all), `./gradlew :infrastructure:persistence:orm:ktorm:test` (one ORM module).
