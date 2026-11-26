plugins {
    id("common-setup")
}

dependencies {
    // project
    implementation(project(":service:core:domain"))
    implementation(project(":service:core:application"))
    testImplementation(testFixtures(project(":service:core:domain")))
    testImplementation(testFixtures(project(":service:core:application")))

    // frameworks
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.test)

    // jackson: request bodies are Kotlin classes without a default constructor
    implementation(libs.jackson.module.kotlin)
}