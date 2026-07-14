group = "fr.maxlego08.menu"

dependencies {
    compileOnly(projects.common)
    compileOnly(libs.paper.api.latest)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    targetCompatibility = JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_25
}

configurations {
    val jvmAttr = Attribute.of("org.gradle.jvm.version", Int::class.javaObjectType)
    apiElements {
        attributes {
            attribute(jvmAttr, 21)
        }
    }
    runtimeElements {
        attributes {
            attribute(jvmAttr, 21)
        }
    }
}