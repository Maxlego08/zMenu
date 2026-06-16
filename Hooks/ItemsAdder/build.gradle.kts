group = "Hooks:ItemsAdder"

repositories {
    maven(url = "https://maven.devs.beer/")
}

dependencies {
    compileOnly(projects.common)
    compileOnly(libs.itemsadder)
}