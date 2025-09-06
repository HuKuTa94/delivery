group = "delivery"
version = "1.0.0-SNAPSHOT"

applyCommonSetup()

plugins {
    applyCommonPlugins()
}

kotlin {
    jvmToolchain(21)
}

subprojects {
    tasks {
        withType<Test> {
            useJUnitPlatform()
        }
    }
}