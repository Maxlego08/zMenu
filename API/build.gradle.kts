plugins {
    id("re.alwyn974.groupez.publish") version "1.0.0"
}

dependencies{
    implementation("net.kyori:adventure-api:4.25.0")
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

publishConfig {
    githubOwner.set("MaxLego08")
}
