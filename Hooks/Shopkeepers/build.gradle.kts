group = "Hooks:Shopkeepers"

repositories {
    maven("https://raw.githubusercontent.com/Shopkeepers/Repository/main/releases/")
}

dependencies {
    compileOnly(projects.api)
    compileOnly(files("libs/ShopkeepersAPI-2.15.1.jar"))
}