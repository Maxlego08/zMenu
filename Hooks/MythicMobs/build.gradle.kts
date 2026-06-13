group = "Hooks:MythicMobs"

repositories {
    mavenCentral()
    maven(url = "https://mvn.lumine.io/repository/maven-public/")
}


dependencies {
    compileOnly(projects.common)
    compileOnly(libs.mythicmobs)
}