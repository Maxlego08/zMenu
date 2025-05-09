group = "Hooks:CraftEngine"

repositories {
    maven("https://repo.momirealms.net/releases/")
}

dependencies {
    compileOnly(project(":API"))
    compileOnly("net.momirealms:craft-engine-core:0.0.52")
    compileOnly("net.momirealms:craft-engine-bukkit:0.0.52")
}