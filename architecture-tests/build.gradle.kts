applyCommonProjectSetup()

plugins {
    applyCommonProjectPlugins()
}

dependencies {
    // all project dependencies
    implementation(project(":configuration"))

    // arch unit
    implementation(Libs.ArchUnit.junit5)
}