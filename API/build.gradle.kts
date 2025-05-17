group "API"

tasks {
    shadowJar {

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

        relocate("com.tcoded.folialib", "fr.maxlego08.menu.hooks.folialib")
        relocate("fr.traqueur.currencies", "fr.maxlego08.menu.hooks.currencies")
        relocate("de.tr7zw.changeme.nbtapi", "fr.maxlego08.menu.hooks.nbtapi")
        relocate("com.cryptomorin.xseries", "fr.maxlego08.menu.hooks.xseries")
        relocate("fr.maxlego08.sarah", "fr.maxlego08.menu.hooks.sarah")
        relocate("net.objecthunter.exp4j", "fr.maxlego08.menu.hooks.exp4j")

        archiveFileName.set("${rootProject.name}-API-${rootProject.version}.jar")
        destinationDirectory.set(rootProject.extra["apiFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }
}