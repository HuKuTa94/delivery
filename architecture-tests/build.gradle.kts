plugins {
    id("common-setup")
}

dependencies {
    // all project dependencies
    implementation(project(":service:bootstrap"))

    // arch unit
    implementation(libs.archunit.junit5)
}