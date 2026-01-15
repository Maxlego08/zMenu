group = "Hooks:ZHead"

repositories {
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    compileOnly(projects.common)
    compileOnly(files("libs/zHead-1.5.jar"))
}