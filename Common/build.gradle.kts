plugins {
    alias(libs.plugins.paperweight)
}

dependencies {
    api(projects.api)
    api(projects.nms.base)
    paperweight.paperDevBundle(libs.versions.paperDevBundle.get())
}