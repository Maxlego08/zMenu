group = "Hooks:Hmccosmetics"

repositories {
    maven (url = ("https://repo.hibiscusmc.com/releases"))
}


dependencies {
    compileOnly(projects.api)
    compileOnly("com.hibiscusmc:HMCCosmetics:2.7.7")
}