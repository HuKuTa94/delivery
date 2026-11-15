# Test fixtures for domain objects

Tests get domain objects from fixture functions in the `testFixtures` source set of the module that owns the class.
Never build them inline.

## Rules

- Tests never call production constructors or factories of domain objects.
  - Wrong: `CourierName.of("John")`
  - Right: `courierName(value = "John")`
  - Exception: tests whose subject *is* that constructor/factory (e.g. validation tests).
- Scope: domain objects only. Primitives, DTOs and test doubles may be created in the test.
- Before writing a fixture, search the owning module's `testFixtures` and reuse a matching one.
- Create a new fixture only when none fits.

## Writing a fixture

- **Location**: `src/testFixtures/kotlin/<same package as production class>/<Class>Fixtures.kt`
- **Form**: top-level functions in a file, not a class or object.
- **Naming**: default state is the class name in lowerCamelCase (`courierName()`);
  other states are `<state><Class>()` (`busyCourier()`).
- **Defaults**: every parameter has a deterministic default, preferably another fixture,
  so tests pass only what matters for the scenario.
- **Reaching a state**: start from a simpler fixture and call real domain functions.
  Use `.apply { }` for mutating methods; chain calls if the method returns a new instance.
  Never use reflection, `copy()` or direct field access to fake a state.
- Fixtures may call production constructors/factories; tests may not.

## Example

```kotlin
package com.example.delivery.courier

fun freeCourier(
    name: CourierName = courierName(),
    location: Location = location(),
): Courier = Courier.create(
    name = name,
    location = location,
)

fun busyCourier(
    name: CourierName = courierName(),
    location: Location = location(),
    orderId: OrderId = orderId(),
): Courier = freeCourier(
    name = name,
    location = location,
).apply {
    assignOrder(orderId)
}
```

Test usage:

```kotlin
// Given
val courier = busyCourier()
```

## Gradle setup

- Enable fixtures in the owning module: `plugins { `java-test-fixtures` }`.
  Its own `test` source set sees them automatically.
- Use them from another module's tests:
  `testImplementation(testFixtures(project(":<module>")))`
- Use them from another module's fixtures:
  `testFixturesImplementation(testFixtures(project(":<module>")))`
