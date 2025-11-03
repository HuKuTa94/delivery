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

    // ktorm
    implementation(Libs.Ktorm.core)

    // mockito
    testImplementation(Libs.Mockito.mockito_kotlin)

    // kotest
    testImplementation(Libs.Kotest.kotest_extensions_spring)

    // spring
    implementation(Libs.Spring.tx)
    implementation(platform(Libs.SpringBoot.bom))
    implementation(Libs.SpringBoot.starter_logging)
}