plugins {
    `maven-publish`
}

group = "${rootProject.group}.api"
extra.properties["sha"]?.let { sha ->
    version = sha
}

tasks {
    shadowJar {
        relocate("com.tcoded.folialib", "fr.maxlego08.menu.hooks.folialib")
        relocate("fr.traqueur.currencies", "fr.maxlego08.menu.hooks.currencies")
        relocate("com.cryptomorin.xseries", "fr.maxlego08.menu.hooks.xseries")

        destinationDirectory.set(rootProject.extra["apiFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${System.getenv("GITHUB_ACTOR")}/zMenu")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("gpr") {
            artifactId = "${rootProject.name}-${project.name}"
            artifact(tasks.shadowJar)
        }
    }
}