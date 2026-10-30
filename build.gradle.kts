import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    `java-library`
    alias(libs.plugins.shadow)
    alias(libs.plugins.groupez.repository)
    alias(libs.plugins.run.paper)
}

group = "fr.maxlego08.menu"
version = "1.1.2.1"

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
        mavenLocal {
            content { includeGroup("fr.maxlego08.sarah") }
        }
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
        maven("https://repo.opencollab.dev/maven-releases")
        maven("https://repo.opencollab.dev/maven-snapshots")
        maven {
            url = uri("https://jitpack.io")
            content {
                excludeGroup("org.spigotmc")
            }
        }
        maven {
            name = "faststatsReleases"
            url = uri("https://repo.faststats.dev/releases")
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
        targetCompatibility = JavaVersion.VERSION_21
        sourceCompatibility = JavaVersion.VERSION_21
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

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
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
        compileOnly(rootLibs.placeholderapi)
        compileOnly(rootLibs.reflections)
        compileOnly(rootLibs.floodgate)

        implementation(rootLibs.sarah)
        implementation(rootLibs.currenciesapi)
        implementation(rootLibs.folialib)
        implementation(rootLibs.paperdispatch)

        implementation(rootLibs.xseries)
        implementation(rootLibs.fastStatsBukkit)
        implementation(rootLibs.exp4j)

        testImplementation(platform(rootLibs.junit.bom))
        testImplementation(rootLibs.junit.jupiter)
        testRuntimeOnly(rootLibs.junit.platform.launcher)

    }

    afterEvaluate {
        if (!plugins.hasPlugin("io.papermc.paperweight.userdev")) {
            val hasAlreadyPaperDependency = configurations.matching { it.name == "compileOnly" || it.name == "implementation" || it.name == "api" }.flatMap { it.dependencies }.any {
                dep -> dep.group == "io.papermc.paper"
            }

            if (!hasAlreadyPaperDependency) {
                dependencies {
                    compileOnly(rootLibs.paper.api)
                }
            }
        }
    }
}

repositories {
    maven(url = "https://repo.codemc.io/repository/maven-public/")
}

runPaper {
    disablePluginJarDetection()
}

/**
 * Configures a test server: its own folder in run/, PlaceholderAPI, and what the runServer.* Gradle properties
 * set, usually in ~/.gradle/gradle.properties:
 *
 * - runServer.versions: the Minecraft versions to create a task for, as a comma list (26.2,1.21.11 by default).
 *   The first one is runServer, the others runServer<version> with _ for the dots, such as runServer1_21_11.
 * - runServer.plugins: a comma list of packetevents, viaversion, luckperms, modrinth:<slug>:<version>,
 *   hangar:<project>:<version> or url:<address>, downloaded next to PlaceholderAPI.
 * - runServer.javaVersion and runServer.javaVendor: the Java toolchain the server runs on (25 and any vendor by
 *   default), such as JetBrains for the JetBrains Runtime.
 * - runServer.jvmArgsExtra: JVM arguments separated by spaces, such as -XX:+AllowEnhancedClassRedefinition.
 * - runServer.javaAgent and runServer.javaAgentArgs: a Java agent jar and its arguments, such as HotswapAgent.
 */
fun RunServer.testServer(version: String) {
    minecraftVersion(version)
    runDirectory.set(layout.projectDirectory.dir("run/$version"))
    systemProperty("terminal.jline", false)
    systemProperty("terminal.ansi", true)
    val plugins = providers.gradleProperty("runServer.plugins").getOrElse("").split(',').map { it.trim() }.filter { it.isNotEmpty() }
    downloadPlugins {
        hangar("PlaceholderAPI", libs.versions.placeholderapi.get())
        for (plugin in plugins) {
            when {
                plugin.equals("packetevents", true) -> modrinth("packetevents", "${libs.versions.packetevents.get()}+spigot")
                plugin.equals("viaversion", true) -> modrinth("viaversion", libs.versions.run.viaversion.get())
                plugin.equals("luckperms", true) -> modrinth("luckperms", libs.versions.run.luckperms.get())
                plugin.startsWith("url:") -> url(plugin.removePrefix("url:"))
                plugin.startsWith("modrinth:") || plugin.startsWith("hangar:") -> {
                    val parts = plugin.split(':')
                    if (parts.size != 3) throw GradleException("runServer.plugins: $plugin must be written ${parts[0]}:<name>:<version>")
                    if (parts[0] == "modrinth") modrinth(parts[1], parts[2]) else hangar(parts[1], parts[2])
                }
                else -> throw GradleException("runServer.plugins: unknown plugin $plugin, use packetevents, viaversion, luckperms, modrinth:<slug>:<version>, hangar:<project>:<version> or url:<address>")
            }
        }
    }

    val javaVersion = providers.gradleProperty("runServer.javaVersion").orNull?.trim()
    val javaVendor = providers.gradleProperty("runServer.javaVendor").orNull?.trim()
    if (!javaVersion.isNullOrEmpty() || !javaVendor.isNullOrEmpty()) {
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(javaVersion?.toIntOrNull() ?: 25))
            if (!javaVendor.isNullOrEmpty()) vendor.set(JvmVendorSpec.matching(javaVendor))
        })
    }
    val extraArgs = providers.gradleProperty("runServer.jvmArgsExtra").getOrElse("").trim()
    if (extraArgs.isNotEmpty()) jvmArgs(extraArgs.split(Regex("\\s+")))
    val javaAgent = providers.gradleProperty("runServer.javaAgent").orNull?.trim()
    if (!javaAgent.isNullOrEmpty()) {
        if (!file(javaAgent).isFile) throw GradleException("runServer.javaAgent: $javaAgent is not a file")
        val agentArgs = providers.gradleProperty("runServer.javaAgentArgs").getOrElse("").trim()
        jvmArgs("-javaagent:$javaAgent" + if (agentArgs.isEmpty()) "" else "=$agentArgs")
    }
}

dependencies {
    api(projects.api)
    api(projects.common)
    api(projects.hooks)
    implementation(projects.nms.v121R1)
    // Java-WebSocket pulls slf4j, which the server already provides - exclude it from the shaded jar to
    // avoid duplicate classes (Paper plugin-remap fails otherwise).
    implementation(libs.java.websocket) {
        exclude(group = "org.slf4j")
    }

    testImplementation(libs.paper.api)
    testRuntimeOnly(libs.sqlite.jdbc)
}

dependencies {
    compileOnly(project(":Hooks:Paper-26"))
    compileOnly(project(":Hooks:Paper-26-3"))
}


tasks {
    shadowJar {
        val paper26 = project(":Hooks:Paper-26")
        dependsOn(paper26.tasks.jar)
        val jarFile = paper26.tasks.jar.get().archiveFile.get().asFile
        from(zipTree(jarFile))
        val paper263 = project(":Hooks:Paper-26-3")
        dependsOn(paper263.tasks.jar)
        from(zipTree(paper263.tasks.jar.get().archiveFile.get().asFile))

        relocate("com.tcoded.folialib", "fr.maxlego08.menu.hooks.folialib")
        relocate("fr.traqueur.currencies", "fr.maxlego08.menu.hooks.currencies")
        relocate("com.cryptomorin.xseries", "fr.maxlego08.menu.hooks.xseries")
        relocate("fr.maxlego08.sarah", "fr.maxlego08.menu.hooks.sarah")
        relocate("net.objecthunter.exp4j", "fr.maxlego08.menu.hooks.exp4j")
        relocate("org.java_websocket", "fr.maxlego08.menu.hooks.java_websocket")
        relocate("fr.robie.paperdispatch", "fr.maxlego08.menu.hooks.paperdispatch")
        relocate("dev.faststats", "fr.maxlego08.menu.hooks.faststats")

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

    val devShadowJar = register<ShadowJar>("devShadowJar") {
        group = "run paper"
        description = "Builds zMenu for the test servers without relocating its libraries, so its classes are the ones IntelliJ compiles and can be hotswapped."
        val release = shadowJar.get()
        from(sourceSets.main.get().output)
        configurations.set(release.configurations)
        exclude(release.excludes)
        val paper26 = project(":Hooks:Paper-26")
        dependsOn(paper26.tasks.jar)
        from(zipTree(paper26.tasks.jar.get().archiveFile.get().asFile))
        val paper263 = project(":Hooks:Paper-26-3")
        dependsOn(paper263.tasks.jar)
        from(zipTree(paper263.tasks.jar.get().archiveFile.get().asFile))
        archiveBaseName.set("zMenu")
        archiveClassifier.set("dev")
        destinationDirectory.set(layout.buildDirectory.dir("dev-jar"))
    }

    val testServerVersions = providers.gradleProperty("runServer.versions").getOrElse("26.3,26.2,26.1.2,26.1,1.21.11")
        .split(',').map { it.trim() }.filter { it.isNotEmpty() }.distinct()
    if (testServerVersions.isEmpty()) throw GradleException("runServer.versions must list at least one Minecraft version")
    testServerVersions.forEachIndexed { index, version ->
        val configure: RunServer.() -> Unit = {
            group = "run paper"
            description = "Run a Paper $version server for plugin testing."
            pluginJars.from(devShadowJar.flatMap { it.archiveFile })
            testServer(version)
        }
        if (index == 0) runServer(configure) else register<RunServer>("runServer" + version.replace('.', '_'), configure)
    }


    processResources {
        from("resources")
        inputs.property("version", project.version)
        filesMatching(listOf("plugin.yml", "paper-plugin.yml")) {
            expand("version" to project.version)
        }
    }
}
