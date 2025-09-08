applyCommonProjectSetup()

plugins {
    applyCommonProjectPlugins()
}

dependencies {
    // project
    implementation(project(":core:domain"))
    implementation(project(":core:application"))

    // frameworks
    implementation(platform(Libs.SpringBoot.bom))
    implementation(Libs.SpringBoot.starter)
}