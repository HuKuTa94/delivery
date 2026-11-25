plugins {
    id("common-setup")
}

dependencies {
    // project
    implementation(project(":service:core:domain"))
    implementation(project(":service:core:application"))
    testImplementation(testFixtures(project(":service:core:domain")))
    testFixturesImplementation(testFixtures(project(":service:core:domain")))
}