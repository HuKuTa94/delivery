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
        add("implementation", PrivateLibs.Kotlin.jdk8)
        add("implementation", PrivateLibs.Kotlin.stdlib)
        add("implementation", PrivateLibs.Kotlin.reflect)
        add("testImplementation", PrivateLibs.Kotlin.test)

        // Kotest
        add("testImplementation", PrivateLibs.Kotest.junit5)
        add("testImplementation", PrivateLibs.Kotest.property)
        add("testImplementation", PrivateLibs.Kotest.engine)
        add("testImplementation", PrivateLibs.Kotest.core_jvm)
        add("testImplementation", PrivateLibs.Kotest.assertions_arrow)
        add("testImplementation", PrivateLibs.Kotest.assertions_arrow_jvm)

        // arrow
        add("implementation", PrivateLibs.ArrowKt.core)
        add("testFixturesImplementation", PrivateLibs.ArrowKt.core)
    }
}

/**
 * Apply common project plugins setup to the module
 * reducing boilerplate code in each build.gradle.kts file
 */
fun PluginDependenciesSpec.applyCommonProjectPlugins() {
    id("io.kotest")                version PrivateLibs.Kotest.version
    id("org.jetbrains.kotlin.jvm") version PrivateLibs.Kotlin.version
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
 * Private libs are used in whole project (each module) and must be applied only in applyCommonProjectSetup function
 */
object PrivateLibs {
    object Kotlin {
        const val version = "2.2.10"
        const val jdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect"
        const val test = "org.jetbrains.kotlin:kotlin-test"
    }
    object Kotest {
        const val version = "6.0.3"
        const val junit5 = "io.kotest:kotest-runner-junit5:$version"
        const val property = "io.kotest:kotest-property:$version"
        const val engine = "io.kotest:kotest-framework-engine:$version"
        const val core_jvm = "io.kotest:kotest-assertions-core-jvm:$version"

        private const val kotest_arrow_version = "2.0.0"
        const val assertions_arrow = "io.kotest.extensions:kotest-assertions-arrow:$kotest_arrow_version"
        const val assertions_arrow_jvm = "io.kotest.extensions:kotest-assertions-arrow-jvm:$kotest_arrow_version"
    }
    object ArrowKt {
        private const val version = "2.1.2"
        const val core = "io.arrow-kt:arrow-core:$version"
    }
}

/**
 * Defines all additional libraries and their versions that are used in whole project in adapters
 */
object Libs {
    object TestContainers {
        private const val version = "1.18.0"
        const val postgresql = "org.testcontainers:postgresql:$version"
    }
    object Kotest {
        const val kotest_extensions_spring = "io.kotest:kotest-extensions-spring-jvm:${PrivateLibs.Kotest.version}"
    }
    object Mockito {
        private const val version = "5.4.0"
        const val mockito_kotlin = "org.mockito.kotlin:mockito-kotlin:$version"
    }
    object ArchUnit {
        private const val version = "1.4.1"
        const val junit5 = "com.tngtech.archunit:archunit-junit5:$version"
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