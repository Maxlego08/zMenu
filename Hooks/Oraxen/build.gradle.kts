group = "Hooks:Oraxen"

repositories {
    maven("https://repo.oraxen.com/releases")
}

dependencies {
    compileOnly(projects.api)
    compileOnly("io.th0rgal:oraxen:1.190.0")
}