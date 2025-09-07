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
    // spring
    implementation(platform(Libs.SpringBoot.bom))
    implementation(Libs.SpringBoot.starter_web)
    implementation(Libs.SpringBoot.starter_jdbc)
    implementation(Libs.SpringBoot.starter_data_jpa)
    implementation(Libs.Spring.kafka)

    // grpc
    implementation(platform(Libs.Grpc.bom))
    implementation(Libs.Grpc.kotlin_stub)
}