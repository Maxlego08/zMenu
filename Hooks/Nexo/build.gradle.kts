group = "Hooks:Nexo"

repositories {
    maven("https://repo.nexomc.com/releases")
}

dependencies {
    compileOnly(projects.common)
    compileOnly(libs.nexo)
    compileOnly(libs.adventure.text.minimessage)
}