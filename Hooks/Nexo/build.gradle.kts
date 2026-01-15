group = "Hooks:Nexo"

repositories {
    maven("https://repo.nexomc.com/releases")
}

dependencies {
    compileOnly(projects.common)
    compileOnly("com.nexomc:nexo:1.9.0")
}