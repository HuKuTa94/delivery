plugins {
    id("common-setup")
    id("protobuf-dependencies")
}

dependencies {
    // project
    implementation(project(":core:domain"))
    implementation(project(":core:application"))

    // spring & logging
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.logging)

    // kafka
    implementation(libs.spring.kafka)
    implementation(libs.kafka.clients)

    // mockito
    testImplementation(libs.mockito.kotlin)

    // integration test: Kafka container
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.testcontainers.kafka)
    testImplementation(libs.spring.kafka.test)
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

tasks.jar {
    archiveBaseName.set("infrastructure-kafka")
}
