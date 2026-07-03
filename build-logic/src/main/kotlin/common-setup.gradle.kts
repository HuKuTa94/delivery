/**
 * Apply common project repository, dependencies and plugins setup to the module
 * reducing boilerplate code in each build.gradle.kts file
 */

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.gitlab.arturbosch.detekt")
    `java-test-fixtures`
}

repositories {
    mavenCentral()
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(libs.findBundle("common").get())
    testImplementation(libs.findBundle("common-test").get())
    testFixturesImplementation(libs.findLibrary("arrow-core").get())
}
