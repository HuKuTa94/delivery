applyCommonSetup()

plugins {
    applyCommonPlugins()
    id("com.google.protobuf") version "0.9.5"
    `java-library`
}

repositories {
    google()
}

val grpcVersion = "1.54.0"
val grpcKotlinVersion = "1.3.0"
val protobufVersion = "3.22.2"
val annotationApiVersion = "6.0.53"

dependencies {
    // project
    implementation(project(":core:domain"))
    implementation(project(":core:application"))

    // Spring & logging
    implementation(platform(Libs.SpringBoot.bom))
    implementation(Libs.SpringBoot.starter_logging)
    implementation(Libs.Spring.kafka)
    implementation(Libs.Kafka.clients)

    // Protobuf & gRPC
    implementation(Libs.Protobuf.java_util)
    implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("com.google.protobuf:protobuf-kotlin:$protobufVersion")
    if (JavaVersion.current().isJava9Compatible) {
        compileOnly("org.apache.tomcat:annotations-api:$annotationApiVersion") // necessary for Java 9+
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }

    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk8@jar"
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

