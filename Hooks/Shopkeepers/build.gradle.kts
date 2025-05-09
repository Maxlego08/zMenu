group = "Hooks:Shopkeepers"

repositories {
    maven("https://raw.githubusercontent.com/Shopkeepers/Repository/main/releases/")
}

dependencies {
    compileOnly(project(":API"))
    compileOnly("com.nisovin.shopkeepers:ShopkeepersAPI:2.15.1")
}