applyCommonSetup()

plugins {
    applyCommonPlugins()
    applyCommonProtobufPlugins()
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:application"))

    // protobuf & grpc
    applyCommonProtobufDependencies()
    implementation(platform(Libs.Grpc.bom))
    implementation(Libs.Grpc.protobuf)
    implementation(Libs.Grpc.kotlin_stub)
    runtimeOnly(Libs.Grpc.netty_shadded)
}

protobuf {
    protoc {
        artifact = Libs.Protobuf.protoc
    }

    plugins {
        create("grpc") {
            artifact = Libs.Grpc.proto_java
        }
        create("grpckt") {
            artifact = Libs.Grpc.proto_kotlin
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
                create("grpckt")
            }
            it.builtins {
                create("kotlin")
            }
        }
    }
}
