applyCommonProjectSetup()

plugins {
    applyCommonProjectPlugins()
    applyCommonProtobufPlugins()
}

dependencies {
    // project
    implementation(project(":core:domain"))
    implementation(project(":core:application"))

    // spring & logging
    implementation(platform(Libs.SpringBoot.bom))
    implementation(Libs.SpringBoot.starter_logging)

    // kafka
    implementation(Libs.Spring.kafka)
    implementation(Libs.Kafka.clients)

    // protobuf
    applyCommonProtobufDependencies()

    // mockito
    testImplementation(Libs.Mockito.mockito_kotlin)

    // integration test: Kafka container
    testImplementation(platform(Libs.TestContainers.bom))
    testImplementation(Libs.Kafka.testcontainers)
    testImplementation(Libs.Spring.kafka_test)
}

protobuf {
    protoc {
        artifact = Libs.Protobuf.protoc
    }

    generateProtoTasks {
        all().forEach {
            it.builtins {
                create("kotlin")
            }
        }
    }
}
