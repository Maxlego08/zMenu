dependencies {
    api(projects.api)
    api(projects.nms.base)
    compileOnly(libs.paper.api)
    testImplementation(libs.paper.api)
}
