group = "delivery"
version = "1.0.0-SNAPSHOT"

applyCommonProjectSetup()

plugins {
    applyCommonProjectPlugins()
}

kotlin {
    jvmToolchain(21)
}

subprojects {
    apply {
        plugin(Plugins.Detekt.plugin)
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
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
            detektPlugins(Plugins.Detekt.formatting)
        }
    }
}