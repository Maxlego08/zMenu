group = "Hooks:CraftEngine"

repositories {
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    compileOnly(projects.api)
    compileOnly("org.geysermc.geyser:api:2.7.0-SNAPSHOT")
}