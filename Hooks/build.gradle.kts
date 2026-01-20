group = "Hooks"

dependencies {
    api(projects.common)

    rootProject.subprojects.filter { it.path.startsWith(":Hooks:") }.forEach { subproject ->
        api(project(subproject.path))
    }
}