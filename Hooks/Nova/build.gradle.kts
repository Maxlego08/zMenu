group = "Hooks:Nova"

repositories {
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    compileOnly(projects.common)
    compileOnly("xyz.xenondevs.nova:nova-api:0.14.10")
}