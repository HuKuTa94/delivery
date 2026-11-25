plugins {
    id("common-setup")
}

dependencies {
    // project
    implementation(project(":service:core:domain"))
    implementation(project(":service:core:application"))

    // frameworks
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.web)
}