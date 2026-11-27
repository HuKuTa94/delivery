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
make test          # ./gradlew test --rerun detekt (always reruns tests; needs Docker for *IT)
make test-smoke    # tests:smoke against the running app on :8080 (other address: -PbaseUrl=...)
make test-e2e      # tests:e2e against the running app via its Kafka :9092 and Postgres :5432
make test-load     # tests:load: all Gatling simulations against the running app on :8080, reading the inbox lag from
                   # its Postgres :5432; fails on a broken SLO; HTML reports in tests/load/build/reports/gatling
make build         # ./gradlew build bootJar -x test -x detekt
make compile       # compileKotlin compileTestKotlin
make detekt        # static analysis only
make run           # build, then docker compose up --wait (returns once the app on :8080 is healthy)
make stop          # stop containers (data in named volumes is kept)
make clean         # ./gradlew clean (build output only, Docker is untouched)
make clean-docker-data   # docker compose down -v: removes containers AND the Postgres/Kafka data volumes
./gradlew :service:core:domain:test   # single module
./gradlew :tests:architecture:test
./gradlew :tests:load:gatlingRun --simulation <FQCN> -PordersPerSecond=50 -PdurationSeconds=300   # one simulation, other load
```

On Windows use `gradlew.bat` (the Makefile auto-detects). Boot jar: `service/bootstrap/build/libs/delivery.jar`.

`test-smoke`, `test-e2e` and `test-load` check the Docker image, not the sources: start it with `make run` and run
`make run` again after code changes. Load runs leave their orders in the Postgres volume (`make clean-docker-data`).

## Rules

- Keep the domain framework-free; technical concerns go to adapters.
- Add dependencies through `gradle/libs.versions.toml`, not inline versions.
- Some comments/README sections are in Russian; keep the existing language of a file when editing it.
