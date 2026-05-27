plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

group = "fr.maxlego08.menu"
version = "1.1.0.7"

dependencies {
    api(projects.api)
    api(projects.nms.base)
    paperweight.paperDevBundle("26.1.2.build.+")
}

paperweight {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = JavaLanguageVersion.of(21)
    }
}