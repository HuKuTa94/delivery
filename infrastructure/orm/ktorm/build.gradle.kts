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

    implementation(project(":infrastructure:orm:commons"))

    // integration test foundation (Testcontainers + Liquibase)
    testImplementation(testFixtures(project(":infrastructure:orm:commons")))

    // jackson — ApplicationEventSerializer runtime in box-event integration tests
    testImplementation(Libs.Jackson.module_kotlin)

    // ktorm
    implementation(Libs.Ktorm.core)
    implementation(Libs.Ktorm.support_postgresql)

    // mockito
    testImplementation(Libs.Mockito.mockito_kotlin)

    // spring
    implementation(Libs.Spring.tx)
    implementation(platform(Libs.SpringBoot.bom))
    implementation(Libs.SpringBoot.starter_logging)
    testImplementation(Libs.Spring.jdbc)
    testImplementation(Libs.Spring.context)
}