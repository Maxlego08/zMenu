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
include("Common")

file("NMS").listFiles()?.forEach { file ->
    if (file.isDirectory && !file.name.equals("build") && !file.name.startsWith(".")) {
        println("Include NMS:${file.name}")
        include(":NMS:${file.name}")
    }
}

file("Hooks").listFiles()?.forEach { file ->
    if (file.isDirectory and !file.name.equals("build") and !file.name.startsWith(".")) {
        println("Include Hooks:${file.name}")
        include(":Hooks:${file.name}")
    }
}