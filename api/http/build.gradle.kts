plugins {
    id("common-setup")
}

dependencies {
    // project
    implementation(project(":core:domain"))
    implementation(project(":core:application"))

    // frameworks
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.web)
}