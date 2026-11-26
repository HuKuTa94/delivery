---
name: architecture
description: Delivery's DDD and hexagonal architecture - module layout, layer dependency rules enforced by ArchUnit, domain model, inbox/outbox event flow, Spring wiring and ORM configuration. Use when adding or moving classes, modules or dependencies, implementing a feature end to end, or touching events, ports, adapters or configuration.
---

# Delivery architecture

Courier-delivery microservice built with Domain-Driven Design and Hexagonal (Ports & Adapters) architecture.
Base package: `github.com.hukuta94.delivery`.

## Module layout

```
service/             Everything that ships in delivery.jar
  core/
    domain/          Pure domain: aggregates, value objects, domain events, business rules. NO frameworks.
    application/     Use cases, ports (output interfaces), queries (CQRS read side), event handlers, inbox/outbox relay
  api/               Primary (driving / input) adapters
    http/            REST controllers (/api/v1/...)
    kafka/           Kafka consumers (basket.confirmed topic, Protobuf JSON)
    scheduler/       @Scheduled jobs that trigger use cases
  infrastructure/    Secondary (driven / output) adapters
    grpc/            GeoService gRPC client -> GetLocationPort
    kafka/           Kafka producer -> BusProducerPort
    persistence/orm/commons/ Shared DB schema constants, inbox/outbox relay jobs, Postgres Testcontainer fixture
    persistence/orm/spring/  Spring Data JPA implementation of repository/query/unit-of-work ports
    persistence/orm/ktorm/   Ktorm implementation of the same ports
    persistence/in-memory/   In-memory repositories
    persistence/migrations/  Liquibase changelog (db/changelog/db.changelog-master.xml)
  bootstrap/         Composition root: Spring Boot app (DeliveryApplication), wires everything with explicit @Bean configs
tests/               Suites that check the service as a whole
  architecture/      ArchUnit tests enforcing layer dependencies and naming
build-logic/         Convention plugins: `common-setup`, `protobuf-dependencies`
```

`service/` and `tests/` are grouping folders, not layers: Gradle paths include them (`:service:core:domain`,
`:tests:architecture`), Kotlin packages don't (`github.com.hukuta94.delivery.core.domain`).

New modules must be registered in `settings.gradle.kts` and normally apply `id("common-setup")`
(Kotlin JVM, Detekt, `java-test-fixtures`, common Arrow/Kotest deps).

## Layer rules (enforced by `tests/architecture`)

Breaking these fails the build. Check `ArchitecturePackages.kt` / `*PackageTest.kt` before adding dependencies.

- **core.domain** depends only on itself + `java..`, `kotlin..`, `org.jetbrains..`, `arrow..`. No Spring, no Jackson, no protobuf.
  - `aggregate..` may contain only `Aggregate`, `DomainEvent`, `ValueObject` subtypes (enums/member classes excepted).
  - `common..` only `ValueObject`s; `rule..` only `BusinessRule`s.
- **core.application**
  - `usecase..` may depend on `port` + domain. Contains `*UseCase` interfaces, `*Impl` classes (implementing `UseCase`), `*Command` classes. Impls live in `usecase/<context>/impl/`.
  - `port..` contains only interfaces with names ending in `Port`.
  - `query..` is self-contained: `*Query` interfaces + `*Response` classes; no `*Impl`s here (implementations are in infrastructure).
  - `event..` may depend on `usecase`, `port`, domain.
- **api..** may depend only on application `event`, `query`, `usecase` (never on ports, domain internals or infrastructure).
- **infrastructure..** may depend on application `port`, `event`, `query` and domain (never on use cases or api).
- Frameworks are allowed in application/adapters; extra framework packages used in tested layers must be added to `FRAMEWORK_PACKAGES`.

Core classes are **not** Spring beans by annotation: they are plain classes instantiated in
`service/bootstrap/.../*Configuration.kt` with `@Bean` methods. When adding a use case, port impl, handler or
rule, also wire it in the matching configuration class.

## Flow

Incoming requests (api adapters) → usecases (commands) → ports (infrastructure adapters) → business logic (aggregates & business rules)

## Domain model

- **Order** (aggregate, id = basket id): `CREATED → ASSIGNED → COMPLETED`; raises `OrderAssignedDomainEvent`, `OrderCompletedDomainEvent`.
- **Courier** (aggregate): `name: CourierName`, `transport: Transport` (speed), `location`, `status FREE/BUSY`; `moveTo()` steps toward a location by transport speed.
- **Location** (value object): grid coordinates `1..10`, Manhattan `distanceTo`; built via `Location.of(x, y): Either<Error, Location>`.
- Business rules (domain services): `DispatchOrderToCourierBusinessRule` (nearest free courier by time), `CompleteOrderBusinessRule`.

Conventions:
- Aggregates extend `Aggregate<ID>`; construction via `companion create(...)` with `internal` constructors; state setters are `private set`.
- Invariant violations / expected failures are `BusinessError` (sealed `Error` classes) returned as Arrow `Either`, not exceptions.
- Aggregates collect events via `raiseDomainEvent`; **use cases** pop them (`popDomainEvents()`) and save them through `OutboxEventRepositoryPort` inside `UnitOfWorkPort.executeInTransaction { }`, not in repository adapters.

## Event flow (inbox / outbox)

1. `api/kafka` `BasketKafkaConsumer` (or `POST /api/v1/orders/`) → `SaveIntegrationEventUseCase` stores a `BasketConfirmedIntegrationDomainEvent` in the **inbox**.
2. `InboxEventMessageRelayJob` (scheduled, `persistence/orm/commons`) → `ApplicationEventPublisher` → `BasketConfirmedIntegrationDomainEventHandler` → `CreateOrderUseCase` (location via gRPC `GetLocationPort`).
3. `OrderScheduler` → `AssignCourierToOrderUseCase`; `CourierScheduler` → `MoveCouriersUseCase`. Domain events go to the **outbox**.
4. `OutboxEventMessageRelayJob` → handlers → `BusProducerPort` → Kafka producer (`OrderStatusChangedContract.proto`).

Relay processes `TO_BE_PROCESSED` and `DELIVERY_ERROR` messages in batches of `delivery.relay.batch-size`.
Events are (de)serialized with Jackson via `ApplicationEventSerializer` / `ApplicationEventDeserializer`; new events need serializer/deserializer coverage and tests.

Proto contracts: `service/api/kafka/src/main/proto`, `service/infrastructure/grpc/src/main/proto`, `service/infrastructure/kafka/src/main/proto` (modules use the `protobuf-dependencies` plugin).

## Configuration

`service/bootstrap/src/main/resources/application.yml`:
- `delivery.orm`: `spring-jpa` | `ktorm` | `inmemory` selects the persistence implementation via `@ConditionalOnProperty`. Any new port implementation must be provided for **each** ORM configuration that needs it (`SpringOrmConfiguration`, `KtormOrmConfiguration`, `InMemoryRepositoryConfiguration`).
- `delivery.geo-location`: `grpc` | `in-memory` selects the `GetLocationPort` implementation the same way:
  `GrpcConfiguration` (`GeoServiceGrpcClient`) or `InMemoryGeoLocationConfiguration` (`InMemoryGetLocation`, a random valid location).
  It is `in-memory` until a real Geo service is available.
- Postgres `localhost:5432`, Kafka `localhost:9092`, Geo gRPC `localhost:8084`, HTTP `:8080`.
- Hardcoded values belong in config properties (see `RelayProperties`, `CorsProperties`).

## Adding a feature

Typical touch points: domain → use case interface + `impl` → port → adapter impl(s) for each ORM →
`bootstrap` beans → Liquibase changeset (if schema changes) → unit tests + `*IT` → run `make test`.
