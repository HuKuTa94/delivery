plugins {
    id("common-setup")
}

dependencies {
    // project
    implementation(project(":service:core:domain"))
    implementation(project(":service:core:application"))

    testImplementation(testFixtures(project(":service:core:domain")))
    testFixturesImplementation(testFixtures(project(":service:core:domain")))

    testImplementation(testFixtures(project(":service:core:application")))
    testFixturesImplementation(testFixtures(project(":service:core:application")))

    implementation(project(":service:infrastructure:persistence:orm:commons"))

    // integration test foundation (Testcontainers + Liquibase)
    testImplementation(testFixtures(project(":service:infrastructure:persistence:orm:commons")))

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