# Load tests

Load, concurrency and resilience tests live in the `load-tests` module. Plan and step list: `backlog/LOAD-TEST-ROADMAP.md`.

- **Harness** in `testFixtures`, package `loadtest`: seeders, `StartGate`, `BarrierRounds`, `OpenModelDriver`,
  `Drain`, invariants `I1–I7`, `LoadReport`, fakes with `FaultKnobs`, `GeoGrpcStubServer`, probes.
- **Scenarios** in `test`, tagged `Load`. Known failing scenarios are tagged `KnownBug` and excluded from runs.
- Base spec: `LoadIntegrationSpec` (connection pool, invariants, adapters per ORM). Needs Docker.
- Run: `make load-test [LOAD_PROFILE=smoke|full]` (`./gradlew :load-tests:loadTest -Dload.profile=...`).
  The regular `test` task runs `!Load & !KnownBug`. Reports go to `load-tests/build/reports/load`.
