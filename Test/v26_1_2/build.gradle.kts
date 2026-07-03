plugins {
    alias(libs.plugins.paperweight)
}

dependencies {
    testImplementation(project(":Test:Base"))
    testImplementation(project(":"))
    testRuntimeOnly(libs.reflections)
    paperweightDevelopmentBundle(libs.paperDevBundle2612)
    testImplementation(libs.paper.api.latest)
    testImplementation(libs.mockbukkit)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    targetCompatibility = JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_25
}

paperweight {
    addServerDependencyTo = configurations.named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).map { setOf(it) }
}