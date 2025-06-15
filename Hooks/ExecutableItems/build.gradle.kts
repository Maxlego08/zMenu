group = "Hooks:ExecutableItems"

repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    compileOnly(projects.api)
    compileOnly("maven.modrinth:SCore:5.25.6.9")
}