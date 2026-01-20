group = "Hooks:ItemsAdder"

repositories {
    maven(url = "https://maven.devs.beer/")
}

dependencies {
    compileOnly(projects.common)
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.1")
}