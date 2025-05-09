group = "Hooks:PacketEvents"

repositories {
    maven ( url = "https://repo.codemc.io/repository/maven-releases/" )
}

dependencies {
    compileOnly(project(":API"))
    compileOnly("com.github.retrooper:packetevents-spigot:2.7.0")
}