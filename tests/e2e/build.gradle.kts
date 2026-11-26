import dev.detekt.gradle.Detekt

plugins {
    id("common-setup")
    id("protobuf-dependencies")
}

dependencies {
    // Contracts of the topics the tests talk to. They are compiled here from the service's .proto files, as any client
    // of the service would do, so the tests don't depend on the service's modules
    testProtobuf(
        files(
            rootDir.resolve("service/api/kafka/src/main/proto"),
            rootDir.resolve("service/infrastructure/kafka/src/main/proto"),
        )
    )

    // Kafka: the tests send basket.confirmed and read order.status.changed
    testImplementation(libs.kafka.clients)

    // Postgres: the tests read the state of the inbox, orders and couriers
    testImplementation(libs.postgresql)
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("kotlin")
            }
        }
    }
}

tasks {
    // detektTest analyses every Kotlin source of the test source set, the code generated from the .proto files too
    named<Detekt>("detektTest") {
        setSource(files("src/test/kotlin"))
    }

    // End-to-end tests need a running application, so they are left out of `test` and run with `e2eTest`
    test {
        enabled = false
    }

    register<Test>("e2eTest") {
        group = "verification"
        description = "Runs end-to-end tests against a running application and its Kafka and Postgres " +
            "(-PkafkaBootstrapServers=..., -PdatabaseUrl=..., -PdatabaseUser=..., -PdatabasePassword=...; " +
            "defaults match docker-compose.yaml)"
        classpath = sourceSets.test.get().runtimeClasspath
        testClassesDirs = sourceSets.test.get().output.classesDirs
        systemProperty(
            "delivery.kafka.bootstrap-servers",
            providers.gradleProperty("kafkaBootstrapServers").getOrElse("localhost:9092")
        )
        systemProperty(
            "delivery.database.url",
            providers.gradleProperty("databaseUrl").getOrElse("jdbc:postgresql://localhost:5432/postgres")
        )
        systemProperty(
            "delivery.database.user",
            providers.gradleProperty("databaseUser").getOrElse("username")
        )
        systemProperty(
            "delivery.database.password",
            providers.gradleProperty("databasePassword").getOrElse("secret")
        )
        // The application under test changes outside of this build, so a previous result never applies
        outputs.upToDateWhen { false }
    }
}
