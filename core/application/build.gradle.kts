description =
"""
Core application layer:
- port/query interfaces for implementing by infrastructure (secondary/output) adapters 
  that can be driven by application layer only;
- use case interfaces for using in API (primary/input) adapters that send request to application;
- event handlers to execute business logic or rules (reacting on domain/integration events);
"""

applyCommonProjectSetup()

plugins {
    applyCommonProjectPlugins()
}

dependencies {
    // project
    implementation(project(":core:domain"))
    testFixturesImplementation(testFixtures(project(":core:domain")))
    testImplementation(testFixtures(project(":core:domain")))

    // frameworks
    implementation(Libs.Jackson.module_kotlin)
    implementation(platform(Libs.SpringBoot.bom))
    implementation(Libs.SpringBoot.starter_logging)
}