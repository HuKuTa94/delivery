plugins {
    id("common-setup")
    id("protobuf-dependencies")
}

dependencies {
    // project
    implementation(project(":service:core:domain"))
    implementation(project(":service:core:application"))

    // spring & logging
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.logging)

    // kafka
    implementation(libs.spring.kafka)
    implementation(libs.kafka.clients)
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
    archiveBaseName.set("api-kafka")
}