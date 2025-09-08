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
    tasks {
        withType<Test> {
            useJUnitPlatform()
        }
    }
}