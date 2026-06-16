group = "Hooks:MythicMobs"

repositories {
    mavenCentral()
    maven(url = "https://nexus.phoenixdevt.fr/repository/maven-public/")
}


dependencies {
    compileOnly(projects.common)
    compileOnly(libs.mmoitems)
    compileOnly(libs.mythicLib)
}