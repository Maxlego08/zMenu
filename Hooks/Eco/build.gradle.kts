group = "Hooks:Eco"

repositories {
    maven("https://repo.auxilor.io/repository/maven-public/")
}


dependencies {
    compileOnly(project(":API"))
    compileOnly("com.willfp:eco:6.53.0")
}