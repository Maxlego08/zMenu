plugins {
    alias(libs.plugins.groupez.publish)
    `java-test-fixtures`
}

dependencies{
    compileOnly(libs.adventure.api)
    compileOnly(libs.adventure.text.minimessage)

    testImplementation(libs.paper.api)
    testFixturesCompileOnly(libs.paper.api)
}

// The test fixtures are shared with the tests of other modules, they are not published.
(components["java"] as AdhocComponentWithVariants).apply {
    withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
    withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
}

rootProject.extra.properties["sha"]?.let { sha ->
    version = sha
}

tasks {
    shadowJar {
        relocate("com.tcoded.folialib", "fr.maxlego08.menu.hooks.folialib")
        relocate("fr.traqueur.currencies", "fr.maxlego08.menu.hooks.currencies")
        relocate("com.cryptomorin.xseries", "fr.maxlego08.menu.hooks.xseries")
        relocate("fr.maxlego08.sarah", "fr.maxlego08.menu.hooks.sarah")
        relocate("net.objecthunter.exp4j", "fr.maxlego08.menu.hooks.exp4j")
        relocate("fr.robie.paperdispatch", "fr.maxlego08.menu.hooks.paperdispatch")

        destinationDirectory.set(rootProject.extra["apiFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }
}

publishConfig {
    githubOwner.set("MaxLego08")
}

// BoostedYAML is in the signatures of the API: an addon needs it to compile, zMenu's plugin loader provides it at runtime
publishing.publications.withType<MavenPublication>().configureEach {
    pom.withXml {
        val root = asNode()
        val dependencies = (root.get("dependencies") as groovy.util.NodeList).firstOrNull() as groovy.util.Node? ?: root.appendNode("dependencies")
        dependencies.appendNode("dependency").apply {
            appendNode("groupId", "dev.dejvokep")
            appendNode("artifactId", "boosted-yaml")
            appendNode("version", libs.versions.boostedyaml.get())
            appendNode("scope", "compile")
        }
    }
}
