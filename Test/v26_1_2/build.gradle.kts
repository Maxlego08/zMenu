plugins {
    alias(libs.plugins.paperweight)
}

dependencies {
    testImplementation(project(":Test:Base"))
    testImplementation(project(":"))
    testImplementation(project(":Hooks:Paper-26"))
    testImplementation(libs.paperApi2612)
    testImplementation(libs.mockbukkit2612)
    testRuntimeOnly(libs.reflections)
    paperweightDevelopmentBundle(libs.paperDevBundle2612)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    targetCompatibility = JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_25
}

paperweight {
    addServerDependencyTo = configurations.named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).map { setOf(it) }
}
