group = "Hooks:Paper"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(projects.common)
    compileOnly(libs.adventure.text.minimessage)
    compileOnly(libs.paper.api)
}