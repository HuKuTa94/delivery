# CLAUDE.md

**Delivery**: a courier-delivery microservice (pet project) built with DDD and Hexagonal architecture.
Kotlin 2.4 / JVM 21, Gradle Kotlin DSL (versions in `gradle/libs.versions.toml`), Spring Boot 3.5 (adapters only),
Arrow `Either` for business errors, PostgreSQL + Liquibase (Spring Data JPA or Ktorm), Kafka (Protobuf), gRPC,
Kotest + Testcontainers + ArchUnit, Gatling (load tests), Detekt.

## Skills

- [`architecture`](.claude/skills/architecture/SKILL.md): module layout, layer rules, domain model, event flow, configuration, feature touch points.
  Read before adding classes, modules, dependencies, events, ports or adapters.
- [`kotlin-code-style`](.claude/skills/kotlin-code-style/SKILL.md): Kotlin formatting and class layout, Detekt. Read before editing any `.kt`/`.kts` file.
- [`testing`](.claude/skills/testing/SKILL.md): index of test references (fixtures, unit, integration). Read before writing tests.

## Git

- write short commit description in one sentence

## Searching

Use `rg`, not `grep`, and exclude generated output:
`rg "OrderRepositoryPort" -g '!**/build/**' -g '!**/out/**'`.
Never edit `**/build/` or `**/out/` (generated protobuf/gRPC and compiled classes).

## Commands

```bash
make test          # ./gradlew detekt test --rerun: Detekt first, then unit tests and *IT (always reruns tests;
                   # needs Docker for *IT)
make test-smoke    # tests:smoke against the app of a clean test stand on :8080
make test-e2e      # tests:e2e through the Kafka :9092 and Postgres :5432 of a clean test stand
make test-load     # tests:load: the Gatling simulations listed in the Makefile through :8080 and Kafka :9092, each on
                   # a clean test stand of its own, reading the inbox from its Postgres :5432; fails on a broken SLO;
                   # HTML reports in tests/load/build/reports/gatling
make test-smoke test-e2e test-load   # the suites in a row, each on a clean test stand; stops at the first failure
make compile       # compileKotlin compileTestKotlin
make run           # dev stand: build, remove the test stand, docker compose up --wait (returns once the app is healthy)
make stop          # stop both stands (data in their volumes is kept)
make clean         # ./gradlew clean (build output only, Docker is untouched)
make clean-docker-data   # down -v for both stands: removes containers AND the Postgres/Kafka data volumes
./gradlew detekt build bootJar -x test   # Detekt, then the build without tests, as `run` and the suites do
./gradlew detekt   # static analysis only
./gradlew :service:core:domain:test   # single module
./gradlew :tests:architecture:test
./gradlew :tests:load:gatlingRun --simulation <FQCN> -PordersPerSecond=50 -PdurationSeconds=300   # one simulation, other load
```

On Windows use `gradlew.bat` (the Makefile auto-detects). Boot jar: `service/bootstrap/build/libs/delivery.jar`.

Stands: both run `docker-compose.yaml` with the shared `delivery-app` image and publish the same ports, so only one
runs at a time. The dev stand (`make run`, project `delivery`) keeps its data. `test-smoke`, `test-e2e` and `test-load`
build the image of the current sources and run on the test stand (project `delivery-test`, volumes of its own): each
starts it from scratch, refuses to run while the dev stand is up (`make stop` first) and leaves it up afterwards to
inspect its data. The suites' Gradle tasks called directly (e.g. one simulation above) hit whatever stand is running.

## Rules

- Keep the domain framework-free; technical concerns go to adapters.
- Add dependencies through `gradle/libs.versions.toml`, not inline versions.
- Some comments/README sections are in Russian; keep the existing language of a file when editing it.
