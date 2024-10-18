plugins {
    id("java")
    application
}

group = "com.das747"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation((project(":library")))
}

val systemProps by extra {
    listOf(
        "commitFinder.cache",
        "commitFinder.cache.type",
        "commitFinder.cache.maxSize"
    )
}


application {
    mainClass.set("com.das747.commitfinder.example.Main")
    applicationDefaultJvmArgs = properties.filterKeys { it in systemProps }.map { "-D${it.key}=${it.value}" }
}