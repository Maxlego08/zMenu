group = "Hooks:Nova"

repositories {
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    compileOnly(project(":API"))
    implementation("xyz.xenondevs.nova:nova-api:0.14.10")
}