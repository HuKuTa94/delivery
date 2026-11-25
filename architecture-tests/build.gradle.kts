plugins {
    id("common-setup")
}

dependencies {
    // all project dependencies
    implementation(project(":service:configuration"))

    // arch unit
    implementation(libs.archunit.junit5)
}