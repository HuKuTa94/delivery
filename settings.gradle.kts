import java.io.File

rootProject.name = "delivery"

includeBuild("build-logic")

// Service: everything that ships in delivery.jar

// Core
include("service:core:domain")
include("service:core:application")

// In/Primary/Drive adapters
include("service:api:http")
include("service:api:kafka")
include("service:api:scheduler")

// Out/Secondary/Driven adapters
include("service:infrastructure:grpc")
include("service:infrastructure:kafka")
include("service:infrastructure:persistence:orm:commons")
include("service:infrastructure:persistence:orm:ktorm")
include("service:infrastructure:persistence:orm:spring")
include("service:infrastructure:persistence:in-memory")
include("service:infrastructure:persistence:migrations")

// Bootstrap: composition root of whole application (Spring Boot app, bean wiring, boot jar)
include("service:bootstrap")

// Tests: suites that check the service as a whole

// Architecture tests of dependencies between modules of whole application
include("tests:architecture")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// Git hooks setup
val gitDir = File(rootDir, ".git")
val hooksDir = File(rootDir, "tools/git/hooks")

when {
    !gitDir.exists() -> println("Skipping Git hooks setup — '.git' directory not found in $rootDir.")
    !hooksDir.exists() -> println("Skipping Git hooks setup — 'tools/git/hooks' directory not found in $rootDir.")
    else -> {
        val desiredPath = hooksDir.relativeTo(rootDir).path
        val currentPath = runCatching {
            ProcessBuilder("git", "config", "--local", "--get", "core.hooksPath")
                .directory(rootDir)
                .redirectErrorStream(true)
                .start()
                .inputStream.bufferedReader()
                .use { it.readText() }
                .trim()
        }.getOrDefault("")

        if (currentPath == desiredPath) {
            println("Git hooks already configured — skipping.")
        } else {
            runCatching {
                ProcessBuilder("git", "config", "--local", "core.hooksPath", desiredPath)
                    .directory(rootDir)
                    .start()
                    .waitFor()
            }
            println("Git hooks path configured: $desiredPath")
        }
    }
}