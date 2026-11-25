# CLAUDE.md

**Delivery**: a courier-delivery microservice (pet project) built with DDD and Hexagonal architecture.
Kotlin 2.2 / JVM 21, Gradle Kotlin DSL (versions in `gradle/libs.versions.toml`), Spring Boot 3.5 (adapters only),
Arrow `Either` for business errors, PostgreSQL + Liquibase, Kafka (Protobuf), gRPC, Kotest + Testcontainers + ArchUnit, Detekt.

## Skills

- [`architecture`](.claude/skills/architecture/SKILL.md): module layout, layer rules, domain model, event flow, configuration, feature touch points.
  Read before adding classes, modules, dependencies, events, ports or adapters.
- [`kotlin-code-style`](.claude/skills/kotlin-code-style/SKILL.md): Kotlin formatting and class layout, Detekt. Read before editing any `.kt`/`.kts` file.
- [`testing`](.claude/skills/testing/SKILL.md): index of test references (fixtures, unit, integration). Read before writing tests.

## Searching

Use `rg`, not `grep`, and exclude generated output:
`rg "OrderRepositoryPort" -g '!**/build/**' -g '!**/out/**'`.
Never edit `**/build/` or `**/out/` (generated protobuf/gRPC and compiled classes).

## Commands

```bash
make test          # ./gradlew test detekt (needs Docker for *IT)
make build         # ./gradlew build bootJar -x test -x detekt
make compile       # compileKotlin compileTestKotlin
make detekt        # static analysis only
make run           # build, then docker compose up (app on :8080)
make stop / clean  # stop / remove containers
make load-test [LOAD_PROFILE=smoke|full]
make load-test-known-bugs     # KnownBug scenarios only; expected to fail, never fails the build
make load-stack-up            # level 5: app + Postgres + Kafka + Toxiproxy + Prometheus + Grafana (:13000)
make load-run SCENARIO=breakpoint|spike|soak|chaos   # generator against the stack; make load-stack-down after
./gradlew :service:core:domain:test   # single module
./gradlew :tests:architecture:test
```

On Windows use `gradlew.bat` (the Makefile auto-detects). Boot jar: `service/bootstrap/build/libs/delivery.jar`.

## Rules

- Keep the domain framework-free; technical concerns go to adapters.
- Add dependencies through `gradle/libs.versions.toml`, not inline versions.
- Some comments/README sections are in Russian; keep the existing language of a file when editing it.
