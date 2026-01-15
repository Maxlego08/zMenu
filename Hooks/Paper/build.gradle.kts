group = "Hooks:Paper"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(projects.common)
    compileOnly("net.kyori:adventure-text-minimessage:4.21.0")
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}