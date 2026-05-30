group = "Hooks:CraftEngine"

repositories {
    maven("https://repo.momirealms.net/releases/")
}

dependencies {
    compileOnly(projects.common)
    compileOnly(libs.craft.engine.core)
    compileOnly(libs.craft.engine.bukkit)
}