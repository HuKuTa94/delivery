plugins {
    id("common-setup")
    alias(libs.plugins.spring.boot)
}

tasks.bootJar {
    archiveFileName.set("delivery.jar")
}

dependencies {
    // project
    // core
    api(project(":service:core:domain"))
    api(project(":service:core:application"))

    // api adapters
    api(project(":service:api:http"))
    api(project(":service:api:kafka"))
    api(project(":service:api:scheduler"))

    // infrastructure adapters
    api(project(":service:infrastructure:grpc"))
    api(project(":service:infrastructure:kafka"))
    api(project(":service:infrastructure:persistence:orm:ktorm"))
    api(project(":service:infrastructure:persistence:orm:spring"))
    api(project(":service:infrastructure:persistence:in-memory"))
    implementation(project(":service:infrastructure:persistence:orm:commons"))
    implementation(project(":service:infrastructure:persistence:migrations"))

    // integration test foundation (Testcontainers + Liquibase)
    testImplementation(testFixtures(project(":service:infrastructure:persistence:orm:commons")))

    // frameworks
    // spring
    implementation(platform(libs.spring.boot.bom))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.kafka)

    // grpc
    implementation(platform(libs.grpc.bom))
    implementation(libs.grpc.kotlin.stub)

    // ktorm
    implementation(libs.ktorm.core)
    implementation(libs.ktorm.support.postgresql)
}
