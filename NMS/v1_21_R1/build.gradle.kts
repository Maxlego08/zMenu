plugins {
    alias(libs.plugins.paperweight)
}

group = "fr.maxlego08.menu.nms.v1_21_R1"

dependencies {
    compileOnly(projects.common)
    paperweightDevelopmentBundle(libs.paperDevBundle121R1)
}
