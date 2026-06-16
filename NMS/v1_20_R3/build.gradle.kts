plugins {
    alias(libs.plugins.paperweight)
}

group = "fr.maxlego08.menu.nms.v1_20_R3"

dependencies {
    compileOnly(projects.common)
    compileOnly(projects.nms.base)
    paperweightDevelopmentBundle(libs.paperDevBundle120R3)
}
