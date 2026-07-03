plugins {
    id("common-setup")
}

dependencies {
    // project
    implementation(project(":core:domain"))
    implementation(project(":core:application"))

    testImplementation(testFixtures(project(":core:domain")))
    testFixturesImplementation(testFixtures(project(":core:domain")))

    testImplementation(testFixtures(project(":core:application")))
    testFixturesImplementation(testFixtures(project(":core:application")))

    implementation(project(":infrastructure:orm:commons"))

    // integration test foundation (Testcontainers + Liquibase)
    testImplementation(testFixtures(project(":infrastructure:orm:commons")))

    // jackson — ApplicationEventSerializer runtime in box-event integration tests
    testImplementation(libs.jackson.module.kotlin)

    // ktorm
    implementation(libs.ktorm.core)
    implementation(libs.ktorm.support.postgresql)

    // mockito
    testImplementation(libs.mockito.kotlin)

    // spring
    implementation(libs.spring.tx)
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.logging)
    testImplementation(libs.spring.jdbc)
    testImplementation(libs.spring.context)
}