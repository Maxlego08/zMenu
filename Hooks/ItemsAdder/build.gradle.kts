group = "Hooks:ItemsAdder"

repositories {
    maven(url = "https://maven.devs.beer/")
}

dependencies {
    compileOnly(projects.api)
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.1")
}