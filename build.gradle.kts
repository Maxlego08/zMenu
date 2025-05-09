plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

group = "fr.maxlego08.menu"
version = "1.0.4.2"

extra.set("targetFolder", file("target/"))
extra.set("apiFolder", file("target-api/"))

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")

    repositories {
        mavenLocal()
        mavenCentral()

        maven(url = "https://jitpack.io")
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    tasks.javadoc {
        options.encoding = "UTF-8"
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT")
    }
}

repositories {

}

dependencies {

    api(project(":API"))

}

tasks {
    shadowJar {

        // relocate("com.tcoded.folialib", "fr.maxlego08.essentials.libs.folialib")

        archiveClassifier = ""

        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
        destinationDirectory.set(rootProject.extra["targetFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }

    compileJava {
        options.release = 21
    }

    processResources {
        from("resources")
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(project.components["java"])
        }
    }
}