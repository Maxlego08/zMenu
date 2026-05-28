group = "Hooks:BreweryX"

repositories {
    maven("https://repo.jsinco.dev/releases")
}

dependencies {
    compileOnly(projects.common)
    compileOnly(libs.breweryx)
}
