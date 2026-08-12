group = "Hooks:ZItems"

repositories {
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.groupez.dev/snapshots")
}

dependencies {
    compileOnly(projects.common)
    compileOnly("fr.traqueur:zitems-api:c5830d0")
}
