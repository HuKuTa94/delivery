import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.*
import org.gradle.plugin.use.PluginDependenciesSpec

/**
 * Apply common project repository and common dependencies setup to the module
 * reducing boilerplate code in each build.gradle.kts file
 */
fun Project.applyCommonProjectSetup() {
    repositories {
        mavenCentral()
    }

    dependencies {
        // kotlin
        add("implementation", Libs.Kotlin.jdk8)
        add("implementation", Libs.Kotlin.stdlib)
        add("implementation", Libs.Kotlin.reflect)
        add("testImplementation", Libs.Kotlin.test)

        // JUnit
        add("testImplementation", platform(Libs.JUnit.bom))
        add("testImplementation", Libs.JUnit.api)
        add("testImplementation", Libs.JUnit.params)
        add("testRuntimeOnly", Libs.JUnit.engine)
        add("testImplementation", Libs.Kotest.junit5)
    }
}

/**
 * Apply common project plugins setup to the module
 * reducing boilerplate code in each build.gradle.kts file
 */
fun PluginDependenciesSpec.applyCommonProjectPlugins() {
    id("org.jetbrains.kotlin.jvm") version "2.2.10"
    `java-test-fixtures`
}

/**
 * Apply common protobuf plugins to the module that are required to generate code from .proto files
 * reducing boilerplate code in each build.gradle.kts file
 */
fun PluginDependenciesSpec.applyCommonProtobufPlugins() {
    id("com.google.protobuf") version "0.9.5"
}

/**
 * Apply common protobuf dependencies to the module that are required to generate code from .proto files
 * reducing boilerplate code in each build.gradle.kts file
 */
fun DependencyHandler.applyCommonProtobufDependencies() {
    add("implementation", Libs.Protobuf.kotlin)
    add("implementation", Libs.Protobuf.java_util)
}

/**
 * Defines all libraries and their versions that are used in whole project
 */
object Libs {
    object Kotlin {
        const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect"
        const val test = "org.jetbrains.kotlin:kotlin-test"
    }
    object JUnit {
        private const val version = "5.10.2"
        const val bom = "org.junit:junit-bom:$version"
        const val api = "org.junit.jupiter:junit-jupiter-api"
        const val engine = "org.junit.jupiter:junit-jupiter-engine"
        const val params = "org.junit.jupiter:junit-jupiter-params"
    }
    object Kotest {
        private const val junit5_version = "6.0.2"
        const val junit5 = "io.kotest:kotest-runner-junit5:$junit5_version"

        private const val arrow_version = "1.1.1"
        const val arrow = "io.kotest.extensions:kotest-assertions-arrow-jvm:$arrow_version"
    }
    object Mockito {
        private const val version = "5.4.0"
        const val mockito_kotlin = "org.mockito.kotlin:mockito-kotlin:$version"
    }
    object ArchUnit {
        private const val version = "1.4.1"
        const val junit5 = "com.tngtech.archunit:archunit-junit5:$version"
    }
    object ArrowKt {
        private const val version = "1.1.5"
        const val core = "io.arrow-kt:arrow-core:$version"
    }
    object Kafka {
        private const val version = "3.9.1"
        const val clients = "org.apache.kafka:kafka-clients:$version"
    }
    object Spring {
        private const val version = "3.3.9"
        const val kafka = "org.springframework.kafka:spring-kafka:$version"
    }
    object SpringBoot {
        private const val version = "3.5.5"
        const val bom = "org.springframework.boot:spring-boot-dependencies:$version"
        const val starter = "org.springframework.boot:spring-boot-starter"
        const val starter_web = "org.springframework.boot:spring-boot-starter-web"
        const val starter_test = "org.springframework.boot:spring-boot-starter-test"
        const val starter_logging = "org.springframework.boot:spring-boot-starter-logging"
        const val starter_jdbc = "org.springframework.boot:spring-boot-starter-jdbc"
        const val starter_data_jpa = "org.springframework.boot:spring-boot-starter-data-jpa"
    }
    object Liquibase {
        private const val version = "4.6.1"
        const val core = "org.liquibase:liquibase-core:$version"
    }
    object Postgresql {
        private const val version = "42.3.1"
        const val postgresql = "org.postgresql:postgresql:$version"
    }
    object Protobuf {
        private const val version = "4.32.0"
        const val kotlin = "com.google.protobuf:protobuf-kotlin:$version"
        const val java_util = "com.google.protobuf:protobuf-java-util:$version"
        const val protoc = "com.google.protobuf:protoc:$version"
    }
    object Grpc {
        private const val version = "1.75.0"
        private const val kotlin_version = "1.4.3"
        const val bom = "io.grpc:grpc-bom:$version"
        const val kotlin_stub = "io.grpc:grpc-kotlin-stub:$kotlin_version"
        const val netty_shadded = "io.grpc:grpc-netty-shaded"
        const val protobuf = "io.grpc:grpc-protobuf"
        const val proto_java = "io.grpc:protoc-gen-grpc-java:$version"
        const val proto_kotlin = "io.grpc:protoc-gen-grpc-kotlin:$kotlin_version:jdk8@jar"
    }
    object Jackson {
        private const val version = "2.13.4"
        const val module_kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:$version"
    }
}