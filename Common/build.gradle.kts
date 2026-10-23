dependencies {
    api(projects.api)
    compileOnly(libs.paper.api)
    testImplementation(libs.paper.api)
    testImplementation(testFixtures(projects.api))
}
