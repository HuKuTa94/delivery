applyCommonSetup()

plugins {
    applyCommonPlugins()
}

dependencies {
    // all project dependencies
    implementation(project(":configuration"))

    // arch unit
    implementation(Libs.ArchUnit.junit5)
}