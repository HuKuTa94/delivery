/**
 * Apply common protobuf dependencies and plugins to the module that are required to generate code from .proto files
 * reducing boilerplate code in each build.gradle.kts file
 */

plugins {
    id("java")
    id("com.google.protobuf")
}

repositories {
    mavenCentral()
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findBundle("protobuf").get())
}
