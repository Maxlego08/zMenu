import java.util.Locale

plugins {
    `maven-publish`
}

rootProject.extra.properties["sha"]?.let { sha ->
    version = sha
}

tasks {
    shadowJar {
        relocate("com.tcoded.folialib", "fr.maxlego08.menu.hooks.folialib")
        relocate("fr.traqueur.currencies", "fr.maxlego08.menu.hooks.currencies")
        relocate("de.tr7zw.changeme.nbtapi", "fr.maxlego08.menu.hooks.nbtapi")
        relocate("com.cryptomorin.xseries", "fr.maxlego08.menu.hooks.xseries")
        relocate("fr.maxlego08.sarah", "fr.maxlego08.menu.hooks.sarah")
        relocate("net.objecthunter.exp4j", "fr.maxlego08.menu.hooks.exp4j")

        destinationDirectory.set(rootProject.extra["apiFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }
}

publishing {
    var repository = System.getProperty("repository.name", "snapshots").replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    repositories {
        maven {
            name = "groupez${repository}"
            url = uri("https://repo.groupez.dev/${repository.lowercase()}")
            credentials {
                username = findProperty("${name}Username") as String? ?: System.getenv("MAVEN_USERNAME")
                password = findProperty("${name}Password") as String? ?: System.getenv("MAVEN_PASSWORD")
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        register<MavenPublication>("groupez${repository}") {
            pom {
                groupId = project.group as String?
                name = "${rootProject.name}-${project.name}"
                artifactId = name.get().lowercase()
                version = project.version as String?

                scm {
                    connection = "scm:git:git://github.com/MaxLego08/${rootProject.name}.git"
                    developerConnection = "scm:git:ssh://github.com/MaxLego08/${rootProject.name}.git"
                    url = "https://github.com/MaxLego08/${rootProject.name}/"
                }
            }
            artifact(tasks.shadowJar)
            artifact(tasks.javadocJar)
            artifact(tasks.sourcesJar)
        }
    }
}
