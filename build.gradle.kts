plugins {
    `java-library`
    alias(libs.plugins.shadow)
    alias(libs.plugins.groupez.repository)
}

group = "fr.maxlego08.menu"
version = "1.1.1.4"

extra.set("targetFolder", file("target/"))
extra.set("apiFolder", file("target-api/"))
extra.set("classifier", System.getProperty("archive.classifier"))
extra.set("sha", System.getProperty("github.sha"))

val rootLibs = libs

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "re.alwyn974.groupez.repository")

    group = "fr.maxlego08.menu"
    version = rootProject.version

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "groupezReleases"
            url = uri("https://repo.groupez.dev/releases")
        }
        maven(url = "https://repo.tcoded.com/releases")
        maven(url = "https://repo.codemc.io/repository/maven-releases/")
        maven(url = "https://repo.codemc.io/repository/maven-snapshots/")
        maven(url = "https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven(url = "https://hub.spigotmc.org/nexus/content/groups/public/")
        maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven(url = "https://libraries.minecraft.net/")
        maven(url = "https://repo.jsinco.dev/releases")
        maven {
            url = uri("https://jitpack.io")
            content {
                excludeGroup("org.spigotmc")
            }
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
        withSourcesJar()
        if (project.name == "API") {
            withJavadocJar()
        }
    }

    tasks.shadowJar {
        exclude("com/cryptomorin/xseries/profiles/**")
        exclude("com/cryptomorin/xseries/profiles/*/*")
        exclude("com/cryptomorin/xseries/reflection/**")
        exclude("com/cryptomorin/xseries/reflection/*/*")
        exclude("com/cryptomorin/xseries/messages/*")
        exclude("com/cryptomorin/xseries/particles/*")
        exclude("com/cryptomorin/xseries/inventory/*")
        exclude("com/cryptomorin/xseries/art/*")
        exclude("com/cryptomorin/xseries/XAttribute*")
        exclude("com/cryptomorin/xseries/XItemFlag*")
        exclude("com/cryptomorin/xseries/XPatternType*")
        exclude("com/cryptomorin/xseries/XBiome*")
        exclude("com/cryptomorin/xseries/NMSExtras*")
        exclude("com/cryptomorin/xseries/NoteBlockMusic*")
        exclude("com/cryptomorin/xseries/SkullCacheListener*")
        exclude("com/cryptomorin/xseries/XTag*")
        exclude("com/cryptomorin/xseries/XPotion*")
        exclude("com/cryptomorin/xseries/XMaterial*")
        exclude("com/cryptomorin/xseries/XItemStack*")
        exclude("com/cryptomorin/xseries/XBlock*")
        exclude("com/cryptomorin/xseries/XEntity*")
        exclude("com/cryptomorin/xseries/XEnchantment*")
        exclude("com/cryptomorin/xseries/SkullUtils*")
        exclude("com/cryptomorin/xseries/ReflectionUtils*")
        exclude("com/cryptomorin/xseries/XWorldBorder*")

        archiveBaseName.set("zMenu")
        archiveAppendix.set(if (project.path == ":") "" else project.name)
        archiveClassifier.set("")
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    if (project.name == "API") {
        tasks.javadoc {
            options.encoding = "UTF-8"
            if (JavaVersion.current().isJava9Compatible)
                (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }

    tasks.test {
        useJUnitPlatform()
    }

    dependencies {
        if (project.name != "Paper" && project.name != "Common") {
            compileOnly(rootLibs.spigot.api)
        }
        compileOnly(rootLibs.placeholderapi)
        compileOnly(rootLibs.reflections)

        implementation(rootLibs.sarah)
        implementation(rootLibs.currenciesapi)
        implementation(rootLibs.folialib)

        implementation(rootLibs.xseries)
        implementation(rootLibs.exp4j)

        testImplementation(platform(rootLibs.junit.bom))
        testImplementation(rootLibs.junit.jupiter)
        testRuntimeOnly(rootLibs.junit.platform.launcher)
    }
}

repositories {
    maven(url = "https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    api(projects.api)
    api(projects.common)
    api(projects.hooks)
    implementation(projects.nms.base)
    implementation(projects.nms.v121R1)
    implementation(projects.nms.v120R4)
    implementation(projects.nms.v120R3)
    implementation(libs.item.nbt.api)
}

tasks {
    shadowJar {
        relocate("com.tcoded.folialib", "fr.maxlego08.menu.hooks.folialib")
        relocate("fr.traqueur.currencies", "fr.maxlego08.menu.hooks.currencies")
        relocate("de.tr7zw.changeme.nbtapi", "fr.maxlego08.menu.hooks.nbtapi")
        relocate("com.cryptomorin.xseries", "fr.maxlego08.menu.hooks.xseries")
        relocate("fr.maxlego08.sarah", "fr.maxlego08.menu.hooks.sarah")
        relocate("net.objecthunter.exp4j", "fr.maxlego08.menu.hooks.exp4j")

        rootProject.extra.properties["sha"]?.let { sha ->
            archiveClassifier.set("${rootProject.extra.properties["classifier"]}-${sha}")
        } ?: run {
            archiveClassifier.set(rootProject.extra.properties["classifier"] as String?)
        }
        destinationDirectory.set(rootProject.extra["targetFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }


    processResources {
        from("resources")
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}
