group = "Hooks:ZItems"

repositories {
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    compileOnly(projects.common)
    compileOnly(files("libs/zItems-1.0.0.jar"))
}