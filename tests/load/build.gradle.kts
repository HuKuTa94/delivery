plugins {
    id("common-setup")
    // Simulations live in the plugin's `gatling` source set (src/gatling/kotlin) and run with `gatlingRun`,
    // so load tests never run as part of `test`
    alias(libs.plugins.gatling)
}

dependencies {
    // Postgres: the simulations read the inbox lag of the running application
    gatlingImplementation(libs.postgresql)
}

// The common detekt sources are main/test/testFixtures only
detekt {
    source.from("src/gatling/kotlin")
}

tasks {
    // `build` compiles the simulations too, so a broken one doesn't wait for the next load test run to show up
    check {
        dependsOn("gatlingClasses")
    }

    // Load tests need a running application (`make run`); the address defaults match docker-compose.yaml.
    // The plugin never considers gatlingRun up-to-date, so every call runs the simulations again
    gatlingRun {
        description = "Runs Gatling simulations against a running application and its Postgres " +
            "(-PbaseUrl=..., -PdatabaseUrl=..., -PdatabaseUser=..., -PdatabasePassword=..., " +
            "-PordersPerSecond=..., -PdurationSeconds=...)"
        systemProperties = mapOf(
            "delivery.base-url" to providers.gradleProperty("baseUrl").getOrElse("http://localhost:8080"),
            "delivery.database.url" to providers.gradleProperty("databaseUrl")
                .getOrElse("jdbc:postgresql://localhost:5432/postgres"),
            "delivery.database.user" to providers.gradleProperty("databaseUser").getOrElse("username"),
            "delivery.database.password" to providers.gradleProperty("databasePassword").getOrElse("secret"),
            "delivery.load.orders-per-second" to providers.gradleProperty("ordersPerSecond").getOrElse("25"),
            "delivery.load.duration-seconds" to providers.gradleProperty("durationSeconds").getOrElse("60"),
        )
    }
}
