group = "Hooks:ZHead"

repositories {
    maven("https://repo.xenondevs.xyz/releases")
}

dependencies {
    compileOnly(project(":API"))
    compileOnly(files("libs/zHead-1.5.jar"))
}