group = "Hooks:Nexo"

repositories {
    maven("https://repo.nexomc.com/releases")
}

dependencies {
    compileOnly(project(":API"))
    compileOnly("com.nexomc:nexo:1.1.0")
}