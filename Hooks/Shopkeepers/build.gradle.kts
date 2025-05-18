group = "Hooks:Shopkeepers"

repositories {
    maven("https://raw.githubusercontent.com/Shopkeepers/Repository/main/releases/")
}

dependencies {
    compileOnly(project(":API"))
    compileOnly(files("libs/ShopkeepersAPI-2.15.1.jar"))
}