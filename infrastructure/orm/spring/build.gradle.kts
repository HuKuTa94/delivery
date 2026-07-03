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
    testImplementation(testFixtures(project(":infrastructure:orm:commons")))
    testImplementation(libs.spring.boot.starter.test)
}