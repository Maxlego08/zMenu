group = "Hooks:BreweryX"

repositories {
    maven("https://repo.jsinco.dev/releases")
}

dependencies {
    compileOnly(projects.api)
    compileOnly("com.dre.brewery:BreweryX:3.6.0")
}
