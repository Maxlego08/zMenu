group = "Hooks:Nova"

repositories {
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    compileOnly(projects.common)
    compileOnly(libs.nova.api)
}