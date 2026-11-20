plugins {
    alias(libs.plugins.jmh)
}

description = "JMH benchmarks of the text pipeline, run on demand: ./gradlew :Benchmarks:jmh"

dependencies {
    jmh(projects.api)
    jmh(projects.hooks.paper)
    jmh(libs.paper.api)
    jmh(libs.paper.minimessageflow)
}

jmh {
    jmhVersion = "1.37"
    warmupIterations = 5
    iterations = 10
    fork = 2
    warmup = "1s"
    timeOnIteration = "1s"
    profilers = listOf("gc")
    providers.gradleProperty("jmh.includes").orNull?.let { includes = listOf(it) }
    providers.gradleProperty("jmh.results").orNull?.let {
        resultFormat = "JSON"
        resultsFile = layout.buildDirectory.file("results/jmh/$it.json")
    }
}
