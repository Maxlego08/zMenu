plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = "fr.maxlego08.menu.nms.v1_20_R4"

dependencies {
    compileOnly(projects.common)
    compileOnly(projects.nms.base)
    paperweightDevelopmentBundle("io.papermc.paper:dev-bundle:1.20.6-R0.1-SNAPSHOT")
}
