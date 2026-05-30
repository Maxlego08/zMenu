plugins {
    alias(libs.plugins.paperweight)
}

group = "fr.maxlego08.menu.nms.v1_21_R1"

dependencies {
    compileOnly(projects.common)
    compileOnly(projects.nms.base)
    paperweightDevelopmentBundle(libs.paperDevBundle121R1)
}
