plugins {
    alias(libs.plugins.groupez.publish)
}

dependencies {
    compileOnly(libs.paper.api)
}

tasks {
    shadowJar {
        destinationDirectory.set(rootProject.extra["apiFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }
}
