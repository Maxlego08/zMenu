group = "Hooks:Nova"

repositories {
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    compileOnly(projects.api)
    compileOnly("xyz.xenondevs.nova:nova-api:0.14.10")
}