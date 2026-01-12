plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.0.0"
    id("re.alwyn974.groupez.repository") version "1.0.0"
}

group = "fr.maxlego08.menu"
version = "1.1.0.7"

extra.set("targetFolder", file("target/"))
extra.set("apiFolder", file("target-api/"))
extra.set("classifier", System.getProperty("archive.classifier"))
extra.set("sha", System.getProperty("github.sha"))

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "re.alwyn974.groupez.repository")

    group = "fr.maxlego08.menu"
    version = rootProject.version

    repositories {
        mavenLocal()
        mavenCentral()

        maven(url = "https://repo.tcoded.com/releases")
        maven(url = "https://jitpack.io")
        maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven(url = "https://libraries.minecraft.net/")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
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

    if (project.name == "API"){
        tasks.javadoc {
            options.encoding = "UTF-8"
            if (JavaVersion.current().isJava9Compatible)
                (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }

    dependencies {
        if (project.name != "Paper") {
            compileOnly("org.spigotmc:spigot-api:1.21.10-R0.1-SNAPSHOT")
        }
        compileOnly("com.mojang:authlib:1.5.26")
        compileOnly("me.clip:placeholderapi:2.11.6")

        implementation("fr.maxlego08.sarah:sarah:1.20")
        implementation("fr.traqueur.currencies:currenciesapi:1.0.10")
        implementation("com.tcoded:FoliaLib:0.5.1")

        implementation("com.github.cryptomorin:XSeries:13.3.0")
        implementation("net.objecthunter:exp4j:0.4.8")
    }
}

repositories {
    maven(url = "https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    api(projects.api)
    api(projects.hooks)
    implementation("de.tr7zw:item-nbt-api:2.15.0")
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
