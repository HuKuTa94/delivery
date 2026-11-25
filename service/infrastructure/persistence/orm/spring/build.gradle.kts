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

    // spring jpa
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.data.jpa)

    // data base
    implementation(libs.liquibase.core)
    runtimeOnly(libs.postgresql)

    // mockito
    testImplementation(libs.mockito.kotlin)

    // jackson
    testImplementation(libs.jackson.module.kotlin)

    // integration test foundation (Testcontainers + Liquibase) + Spring test support
    testImplementation(testFixtures(project(":service:infrastructure:persistence:orm:commons")))
    testImplementation(libs.spring.boot.starter.test)
}