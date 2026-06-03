group = "Hooks:Eco"

repositories {
    maven {
        name = "citizensnpcs"
        url = uri("https://maven.citizensnpcs.co/repo")
    }
}

dependencies {
    compileOnly(projects.common)
    compileOnly(libs.denizen)
}