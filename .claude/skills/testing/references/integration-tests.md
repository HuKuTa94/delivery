# Integration tests

- Suffix **`*IT`**; they need **Docker** (Testcontainers: `postgres:15.3`, `apache/kafka:3.8.0`;
  Ryuk disabled, containers stopped via shutdown hook). They fail, not skip, without Docker.
- Postgres fixture: `PostgresTestContainer` in `infrastructure/orm/commons` testFixtures
  (singleton, schema from Liquibase, `cleanData` before each test).
- Base specs: `KtormIntegrationSpec`, `SpringJpaIntegrationSpec`.
- Style rules from [unit-tests.md](unit-tests.md) apply.

Run: `make test` (all), `./gradlew :infrastructure:orm:ktorm:test` (one ORM module).
