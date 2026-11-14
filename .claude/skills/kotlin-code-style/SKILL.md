---
name: kotlin-code-style
description: Project Kotlin style rules for return types, parameter and argument layout, named arguments, and class member order. Use whenever writing, editing or reviewing .kt or .kts files in this repo.
---

# Kotlin code style

Apply to new and changed code only. Do not reformat untouched code.

## 1. Single-expression functions

Use an expression body. Omit the return type when it would only repeat what the
right-hand side already shows: a constructor call of that exact type, or a literal.
Declare it for everything else, including factory methods (`Foo.create(...)`),
which may return a nullable, wrapped or subtype.

```kotlin
// good
fun foo(id: Id) = Foo(id)

// bad: block body for one expression
fun foo(id: Id): Foo {
    return Foo(id)
}

// good
fun message(
    id: Id,
    payload: Payload,
) = Message(
    id = id,
    payload = payload,
)

// bad: type name repeated on both sides
fun message(
    id: Id,
    payload: Payload,
): Message = Message(
    id = id,
    payload = payload,
)

// good
fun names(): List<String> = users.map { it.name }
fun courier(name: CourierName): Courier = Courier.create(name = name)

// bad: inferred type is not obvious
fun names() = users.map { it.name }
fun courier(name: CourierName) = Courier.create(name = name)
```

Always declare the type when it should be wider than the inferred one
(`List` instead of `ArrayList`) or may be nullable.

### Wrapping long expression bodies

Wrap a single-expression function when it exceeds 120 characters or its lambda
body contains a nested call. Keep the expression body (never convert to a block
body with `return`). In order of preference:

1. Lambda: move its body to the next line.
2. 2+ arguments: one named argument per line (see §3).
3. Call chain: one call per line, each starting with `.` (see §3, Fluent chains).
4. Otherwise: break after `=` and indent the expression.

```kotlin
// good: lambda body on its own line
fun locations(count: Int): List<Location> = List(count) {
    randomLocation(seed = SEED)
}

// good: chain, one call per line
fun activeNames(): List<String> = users
    .filter { it.isActive }
    .map { it.name }

// good: break after `=`
fun defaultRoute(): Route =
    deliveryRouteRepository.findDefaultRouteFor(warehouse = mainWarehouse)

// bad: nested call inside a one-line lambda
fun locations(count: Int): List<Location> = List(count) { randomLocation(seed = SEED) }

// bad: converted to a block body just to wrap
fun locations(count: Int): List<Location> {
    return List(count) { randomLocation(seed = SEED) }
}
```

## 2. Parameter declarations

- 1 parameter: single line, no trailing comma (except class headers, see below).
- 2+ parameters: one per line, trailing comma after the last
  (adding a parameter later doesn't touch the previous line in VCS history).

Applies to functions, constructors and data class properties.

```kotlin
// good
fun foo(param1: Param1) {}
fun foo(
    param1: Param1,
    param2: Param2,
) {}

// bad: 2+ parameters on one line
fun foo(param1: Param1, param2: Param2) {}

// bad: single parameter split over lines
fun foo(
    param1: Param1,
) {}
```

### Class headers

When a class calls a supertype constructor with arguments, always wrap the header:
- primary constructor parameters: one per line, trailing comma, even when there is only one
- supertype call: named arguments, one per line, trailing comma, even when there is only one
  (positional if the supertype is a Java class, see §3)

A supertype call without arguments (`: Base()`) doesn't trigger this rule.

```kotlin
// good
class Foo private constructor(
    param1: Param1,
    param2: Param2,
) : Bar(
    param1 = param1,
    param2 = param2,
) {}

// good
class Foo private constructor(
    param1: Param1,
) : Bar(
    param1 = param1,
) {}

// bad: constructor parameters on one line
class Foo private constructor(param1: Param1, param2: Param2) : Bar(
    param1 = param1,
    param2 = param2,
) {}

// bad: whole header on one line
class Foo private constructor(param1: Param1, param2: Param2) : Bar(param1, param2) {}
```

## 3. Call arguments

With 2+ arguments, use named arguments, one per line, with a trailing comma.

> **Note:** named arguments work only when calling Kotlin declarations. Java methods
> and constructors (including Java supertypes and Java libraries) don't support them:
> pass arguments positionally, still one per line with a trailing comma.

```kotlin
foo(
    param1 = Param1(),
    param2 = Param2(),
)
```

Exceptions (positional is fine):
- Java methods and constructors (see note above)
- calls inside a fluent chain (see Fluent chains below)
- trailing lambdas
- varargs and collection builders: `listOf(a, b)`, `mapOf(...)`
- functions where order is self-evident: `maxOf(a, b)`, `Pair(a, b)`

### Fluent chains (builders, DSLs, queries)

In a call chain, put each call on its own line starting with `.` and keep its
arguments positional on that line. The method name already says what the
arguments mean, and one call per line keeps the chain scannable as a column.
This overrides the named-argument rule above, including for the call that
starts the chain.

Use named arguments in a chain only when a call no longer fits on one line or
its positional arguments would be ambiguous (e.g. two booleans).

```kotlin
// good
val foo = Foo("name", param)
    .with("key1", value1)
    .with("key2", value2)
    .with("key3", value3)

// bad: every call split into named arguments, the chain is lost
val foo = Foo(
    name = "name",
    param = param,
).with(
    key = "key1",
    value = value1,
).with(
    key = "key2",
    value = value2,
)
```

## 4. Class layout

Top to bottom:
1. Properties and `init` blocks
2. Secondary constructors
3. `const` → `val` → `var`
4. Functions, grouped by visibility: `public` → `protected` → `internal` → `private`
5. Nested classes
6. `companion object`: always last

```kotlin
class Foo(private val repo: Repo) : FooBase() {
    val name = "foo"
    private val state = "bar"

    fun create() {}
    protected override fun base() {}
    internal fun sync() {}
    private fun validate() {}

    companion object {
        fun of(repo: Repo) = Foo(repo)
    }
}
```

### Function order (call chain)

Within each visibility group from above, order functions top-down by call order:
a caller comes before the functions it calls. A function used by only one other
function goes directly below that caller. A function called from several places
goes after all of them, at the point it is first called.

```kotlin
// good: top-down call order
class Foo(private val repo: Repo) : FooBase() {
    fun fun1() {
        fun2()
    }

    fun fun2() {
        fun3()
    }

    private fun fun3() {}
}

// bad: callee declared before its caller
class Foo(private val repo: Repo) : FooBase() {
    private fun fun3() {}

    fun fun2() {
        fun3()
    }

    fun fun1() {
        fun2()
    }
}
```
