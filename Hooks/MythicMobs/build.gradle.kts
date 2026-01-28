group = "Hooks:MythicMobs"

repositories {
    mavenCentral()
    maven(url = "https://mvn.lumine.io/repository/maven-public/")
}


dependencies {
    compileOnly(projects.common)
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
}