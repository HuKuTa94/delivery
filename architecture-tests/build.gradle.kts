applyCommonSetup()

plugins {
    applyCommonPlugins()
}

dependencies {
    // project
    // core
    implementation(project(":core:domain"))
    implementation(project(":core:application"))

    // api adapters
    implementation(project(":api:http"))
    implementation(project(":api:kafka"))
    implementation(project(":api:scheduler"))

    // infrastructure adapters
    implementation(project(":infrastructure:orm"))
    implementation(project(":infrastructure:grpc"))
    implementation(project(":infrastructure:kafka"))
    implementation(project(":infrastructure:in-memory"))

    // frameworks
    // arch unit
    implementation(Libs.ArchUnit.junit5)
}