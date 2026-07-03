# Delivery

## Integration tests (Testcontainers)

Integration tests (classes suffixed `*IT`) run against a real PostgreSQL started via Testcontainers.
The schema is applied from the Liquibase changelog of `:infrastructure:persistence:migrations`.

**Docker is required** to run them. Without a running Docker daemon these tests fail (they are not
silently skipped). The shared foundation lives in `:infrastructure:orm:commons` test fixtures
(`PostgresTestContainer`): a singleton container per test JVM, reused across specs; mutable tables are
truncated before each test.

Run all tests (unit + integration):

```bash
./gradlew test
```

Run integration tests of a single module, e.g.:

```bash
./gradlew :infrastructure:orm:ktorm:test
./gradlew :infrastructure:orm:spring:test
./gradlew :configuration:test
```

Kafka integration tests (`:infrastructure:kafka`) start an Apache Kafka (KRaft) container and verify the
producer against a real broker (`OrderKafkaProducerIT`).

Notes:
- Containers use pinned images (`postgres:15.3`, `apache/kafka:3.8.0`); the Testcontainers Ryuk reaper is
  disabled (`TESTCONTAINERS_RYUK_DISABLED=true`), containers are stopped via the JVM shutdown hook.

## Тесты
Конвенция по написанию тестов.

### Глоссарий:
- **sut (System Under Test)** — тестируемая система или класс.
  Термин используется для обозначения тестируемого объекта, который создаётся в каждом тесте в секции **Given**,
  либо задаётся в поле тестового класса и используется всеми тестами.
- **test fixtures** — фабрики для создания валидных доменных объектов, используемых в тестах бизнес-логики. 
  Они доступны только из тестов и других **test fixtures**.

### Структура тестов
Тесты строятся по схеме **GWT (AAA)**:
- **Given (Arrange)** — секция подготовки данных и окружения.
- **When (Act)** — секция действия. Если здесь более одной строки, это признак проблем с API тестируемой системы.
- **Then (Assert)** — секция проверки ожидаемого результата.

### Именование тестов:
- Не используйте жесткую структуру именования тестов.
- Не включайте имя тестируемого метода или функции в название теста.
- Присваивайте имена тестам так, как если бы вы описывали сценарий непрограммисту, знакомому
  с предметной областью.

### Тестовые данные для тестов:
- Переиспользование кода инициализации тестовых данных (не являющихся **sut**) осуществляется 
  через **фабричные функции** из **test fixtures**.
- на каждый доменный класс (кроме enum) создается отдельный **kotlin** файл с набором фабричных функций
- в названии фабричной функции должно отражаться состояние создаваемого объекта
