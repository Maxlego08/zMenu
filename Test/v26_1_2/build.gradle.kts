plugins {
    alias(libs.plugins.paperweight)
}

dependencies {
    testImplementation(project(":Test:Base"))
    testImplementation(project(":"))
    testImplementation(project(":Hooks:Paper-26"))
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

tasks.test {
    // Each test class boots zMenu with MockBukkit, and some of its helpers can only be set up once per JVM.
    forkEvery = 1
}

paperweight {
    addServerDependencyTo = configurations.named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).map { setOf(it) }
}