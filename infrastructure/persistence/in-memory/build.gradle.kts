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
}