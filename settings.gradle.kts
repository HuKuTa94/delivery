import java.io.File

rootProject.name = "delivery"

// Core
include("core:domain")
include("core:application")

// In/Primary/Drive adapters
include("api:http")
include("api:kafka")
include("api:scheduler")

// Out/Secondary/Driven adapters
include("infrastructure:grpc")
include("infrastructure:kafka")
include("infrastructure:orm:commons")
include("infrastructure:orm:ktorm")
include("infrastructure:orm:spring")
include("infrastructure:persistence:in-memory")
include("infrastructure:persistence:migrations")

// Configuration of whole application
include("configuration")

// Architecture tests of dependencies between modules of whole application
include("architecture-tests")

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