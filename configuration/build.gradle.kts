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
    api(project(":core:domain"))
    api(project(":core:application"))

    // api adapters
    api(project(":api:http"))
    api(project(":api:kafka"))
    api(project(":api:scheduler"))

    // infrastructure adapters
    api(project(":infrastructure:grpc"))
    api(project(":infrastructure:kafka"))
    api(project(":infrastructure:orm:ktorm"))
    api(project(":infrastructure:orm:spring"))
    api(project(":infrastructure:persistence:in-memory"))
    implementation(project(":infrastructure:orm:commons"))
    implementation(project(":infrastructure:persistence:migrations"))

    // integration test foundation (Testcontainers + Liquibase)
    testImplementation(testFixtures(project(":infrastructure:orm:commons")))

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
