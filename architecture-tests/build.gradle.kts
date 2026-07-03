plugins {
    id("common-setup")
}

dependencies {
    // all project dependencies
    implementation(project(":configuration"))

    // arch unit
    implementation(libs.archunit.junit5)
}