rootProject.name = "zMenu"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven {
            name = "groupezReleases"
            url = uri("https://repo.groupez.dev/releases")
        }
        gradlePluginPortal()
    }
}


include("API")

file("Hooks").listFiles()?.forEach { file ->
    if (file.isDirectory and !file.name.equals("build")) {
        println("Include Hooks:${file.name}")
        include(":Hooks:${file.name}")
    }
}
