plugins {
    id("common-setup")
    id("protobuf-dependencies")
}

dependencies {
    // project
    implementation(project(":core:domain"))
    implementation(project(":core:application"))

    // protobuf & grpc
    implementation(platform(libs.grpc.bom))
    implementation(libs.grpc.protobuf)
    implementation(libs.grpc.kotlin.stub)
    runtimeOnly(libs.grpc.netty.shaded)
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    plugins {
        create("grpc") {
            artifact = libs.grpc.protoc.gen.java.get().toString()
        }
        create("grpckt") {
            artifact = libs.grpc.protoc.gen.kotlin.get().toString()
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
