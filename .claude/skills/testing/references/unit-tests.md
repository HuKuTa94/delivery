# Unit tests

- Kotest `StringSpec`; structure every test as `// Given`, `// When`, `// Then` (When should be one line).
- Name tests as a scenario a domain expert would understand; don't put the method name in the test name.
- The object under test is `sut`.
- Test data comes from testFixtures factory functions, see [test-fixtures.md](test-fixtures.md).
- Use fakes from `core/application` testFixtures (`OrderRepositoryFake`, `UnitOfWorkFake`, …) for use case tests rather than mocks.
- Use `assertSoftly` for multiple assertions; property tests via `kotest-property` where useful.

Run a single module: `./gradlew :core:domain:test`.
