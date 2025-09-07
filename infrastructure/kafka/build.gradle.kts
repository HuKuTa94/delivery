applyCommonSetup()

plugins {
    applyCommonPlugins()
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
