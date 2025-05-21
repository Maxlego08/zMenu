group = "Hooks:Paper"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(projects.api)
    compileOnly("net.kyori:adventure-text-minimessage:4.21.0")
}