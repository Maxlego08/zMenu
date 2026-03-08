group = "Hooks:MythicMobs"

repositories {
    mavenCentral()
    maven(url = "https://nexus.phoenixdevt.fr/repository/maven-public/")
}


dependencies {
    compileOnly(projects.common)
    compileOnly("net.Indyuce:MMOItems-API:6.9.5-SNAPSHOT")
}