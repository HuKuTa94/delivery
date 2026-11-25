description =
"""
Core application layer:
- port/query interfaces for implementing by infrastructure (secondary/output) adapters 
  that can be driven by application layer only;
- use case interfaces for using in API (primary/input) adapters that send request to application;
- event handlers to execute business logic or rules (reacting on domain/integration events);
"""

plugins {
    id("common-setup")
}

dependencies {
    // project
    implementation(project(":service:core:domain"))
    testFixturesImplementation(testFixtures(project(":service:core:domain")))
    testImplementation(testFixtures(project(":service:core:domain")))

    // frameworks
    implementation(libs.jackson.module.kotlin)
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.logging)
}