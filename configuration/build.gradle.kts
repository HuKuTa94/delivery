applyCommonProjectSetup()

plugins {
    applyCommonProjectPlugins()
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

    // ktorm
    implementation(Libs.Ktorm.core)
    implementation(Libs.Ktorm.support_postgresql)
}