group = "Hooks:Hmccosmetics"

repositories {
    maven (url = ("https://repo.hibiscusmc.com/releases"))
}


dependencies {
    compileOnly(projects.common)
    compileOnly(libs.hmccosmetics)
}