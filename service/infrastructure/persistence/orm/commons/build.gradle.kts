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

    // spring context
    implementation(libs.spring.tx)
    implementation(libs.spring.context)
    testImplementation(libs.spring.jdbc)

    // coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Integration-test foundation (Testcontainers + Liquibase), exposed to consumers via testFixtures.
    testFixturesApi(platform(libs.testcontainers.bom))
    testFixturesApi(libs.testcontainers.postgresql)
    testFixturesApi(libs.postgresql)
    testFixturesApi(libs.liquibase.core)
    testFixturesApi(project(":service:infrastructure:persistence:migrations"))
}