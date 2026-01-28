group = "Hooks:CraftEngine"

repositories {
    maven("https://repo.momirealms.net/releases/")
}

dependencies {
    compileOnly(projects.common)
    compileOnly("net.momirealms:craft-engine-core:0.0.52")
    compileOnly("net.momirealms:craft-engine-bukkit:0.0.52")
}