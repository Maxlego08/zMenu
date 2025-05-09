group = "Hooks:ItemsAdder"

repositories {
    maven(url = "https://maven.devs.beer/")
}

dependencies {
    compileOnly(project(":API"))
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.1")
}