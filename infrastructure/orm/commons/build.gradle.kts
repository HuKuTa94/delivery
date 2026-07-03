applyCommonProjectSetup()

plugins {
    applyCommonProjectPlugins()
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
    implementation(Libs.Spring.tx)
    implementation(Libs.Spring.context)

    // Integration-test foundation (Testcontainers + Liquibase), exposed to consumers via testFixtures.
    testFixturesApi(platform(Libs.TestContainers.bom))
    testFixturesApi(Libs.TestContainers.postgresql)
    testFixturesApi(Libs.Postgresql.postgresql)
    testFixturesApi(Libs.Liquibase.core)
    testFixturesApi(project(":infrastructure:persistence:migrations"))
}