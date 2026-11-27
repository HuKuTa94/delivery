plugins {
    id("common-setup")
    // Simulations live in the plugin's `gatling` source set (src/gatling/kotlin) and run with `gatlingRun`,
    // so load tests never run as part of `test`
    alias(libs.plugins.gatling)
}

dependencies {
    // Kafka: BasketConfirmedConsumerSimulation sends basket.confirmed
    gatlingImplementation(libs.kafka.clients)

    // Postgres: the simulations read the inbox of the running application
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

    // Load tests need a running application (`make test-load` starts a clean test stand for each simulation);
    // the address defaults match docker-compose.yaml.
    // The plugin never considers gatlingRun up-to-date, so every call runs the simulations again
    gatlingRun {
        description = "Runs Gatling simulations against a running application and its Kafka and Postgres " +
            "(-PbaseUrl=..., -PkafkaBootstrapServers=..., -PdatabaseUrl=..., -PdatabaseUser=..., " +
            "-PdatabasePassword=..., -PordersPerSecond=..., -PbasketEventsPerSecond=..., -PdurationSeconds=...)"
        systemProperties = mapOf(
            "delivery.base-url" to providers.gradleProperty("baseUrl").getOrElse("http://localhost:8080"),
            "delivery.kafka.bootstrap-servers" to providers.gradleProperty("kafkaBootstrapServers")
                .getOrElse("localhost:9092"),
            "delivery.database.url" to providers.gradleProperty("databaseUrl")
                .getOrElse("jdbc:postgresql://localhost:5432/postgres"),
            "delivery.database.user" to providers.gradleProperty("databaseUser").getOrElse("username"),
            "delivery.database.password" to providers.gradleProperty("databasePassword").getOrElse("secret"),
            "delivery.load.orders-per-second" to providers.gradleProperty("ordersPerSecond").getOrElse("25"),
            "delivery.load.basket-events-per-second" to providers.gradleProperty("basketEventsPerSecond")
                .getOrElse("1000"),
            "delivery.load.duration-seconds" to providers.gradleProperty("durationSeconds").getOrElse("60"),
        )
    }
}
