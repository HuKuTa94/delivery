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

    // spring context
    implementation(libs.spring.tx)
    implementation(libs.spring.context)

    // Integration-test foundation (Testcontainers + Liquibase), exposed to consumers via testFixtures.
    testFixturesApi(platform(libs.testcontainers.bom))
    testFixturesApi(libs.testcontainers.postgresql)
    testFixturesApi(libs.postgresql)
    testFixturesApi(libs.liquibase.core)
    testFixturesApi(project(":infrastructure:persistence:migrations"))
}