group = "Hooks:CraftEngine"

repositories {
    maven("https://repo.momirealms.net/releases/")
}

dependencies {
    compileOnly(projects.common)
    compileOnly("net.momirealms:craft-engine-core:26.5")
    compileOnly("net.momirealms:craft-engine-bukkit:26.5")
}