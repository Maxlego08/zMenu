group = "Hooks:ViaVersion"

repositories {
    maven("https://repo.viaversion.com")
}

dependencies {
    compileOnly(projects.api)
    compileOnly(libs.paper.api)
    compileOnly(libs.viaversion)
}
