group = "Hooks"

dependencies {
    api(projects.common)

    rootProject.subprojects.filter { it.path.startsWith(":Hooks:") && it.name != "Paper-26" && it.name != "Paper-26-3" }.forEach { subproject ->
        api(project(subproject.path))
    }
}