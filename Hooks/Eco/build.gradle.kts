group = "Hooks:Eco"

repositories {
    maven("https://repo.auxilor.io/repository/maven-public/")
}


dependencies {
    compileOnly(projects.common)
    compileOnly(libs.eco)
}