group = "Hooks"

dependencies {
    api(projects.common)

    rootProject.subprojects.filter { it.path.startsWith(":Hooks:") && it.name != "Paper-26" }.forEach { subproject ->
        api(project(subproject.path))
    }
}