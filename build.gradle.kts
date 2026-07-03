group = "delivery"
version = "1.0.0-SNAPSHOT"

plugins {
    id("common-setup")
}

kotlin {
    jvmToolchain(21)
}

subprojects {
    apply {
        plugin(rootProject.libs.plugins.detekt.get().pluginId)
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
            // Testcontainers: use the locally available image and skip the Ryuk reaper
            // (containers are stopped via the JVM shutdown hook). Integration tests require Docker.
            environment("TESTCONTAINERS_RYUK_DISABLED", "true")
        }
    }

    detekt {
        config.from(files("$rootDir/tools/detekt/detekt-config.yaml"))
        buildUponDefaultConfig = true

        source.from(
            "src/main/kotlin",
            "src/test/kotlin",
            "src/testFixtures/kotlin",
        )

        dependencies {
            detektPlugins(rootProject.libs.detekt.formatting)
        }
    }
}