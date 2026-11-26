plugins {
    id("common-setup")
}

dependencies {
    // coroutines: await HTTP responses without blocking the test thread
    testImplementation(libs.kotlinx.coroutines.core)
}

tasks {
    // Smoke tests need a running application, so they are left out of `test` and run with `smokeTest`
    test {
        enabled = false
    }

    register<Test>("smokeTest") {
        group = "verification"
        description = "Runs smoke tests against a running application (-PbaseUrl=..., default http://localhost:8080)"
        classpath = sourceSets.test.get().runtimeClasspath
        testClassesDirs = sourceSets.test.get().output.classesDirs
        systemProperty(
            "delivery.base-url",
            providers.gradleProperty("baseUrl").getOrElse("http://localhost:8080")
        )
        // The application under test changes outside of this build, so a previous result never applies
        outputs.upToDateWhen { false }
    }
}
