group = "Hooks:PacketEvents"

dependencies {
    compileOnly(projects.common)
    compileOnly(libs.packetevents)

    testImplementation(projects.common)
    testImplementation(libs.packetevents)
    testImplementation(libs.paper.api)
}
