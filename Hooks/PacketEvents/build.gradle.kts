group = "Hooks:PacketEvents"

repositories {
    maven ( url = "https://repo.codemc.io/repository/maven-releases/" )
}

dependencies {
    compileOnly(projects.common)
    compileOnly("com.github.retrooper:packetevents-spigot:2.7.0")
}