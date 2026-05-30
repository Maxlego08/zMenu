group = "Hooks:Bedrock"

repositories {
    maven("https://repo.opencollab.dev/maven-releases")
    maven("https://repo.opencollab.dev/maven-snapshots")
}

dependencies {
    compileOnly(projects.api)
    compileOnly(libs.floodgate)
}