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
    testImplementation("org.testng:testng:7.9.0")
    testImplementation("org.mockito:mockito-core:3.+")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    compileOnly("org.jetbrains:annotations:25.0.0")
    testCompileOnly("org.jetbrains:annotations:25.0.0")
}

val systemProps by extra {
    listOf(
        "commitFinder.cache",
        "commitFinder.cache.type",
        "commitFinder.cache.maxSize"
    )
}

tasks.test {
    useTestNG()
}

application {
    mainClass.set("com.das747.commitfinder.Main")
    applicationDefaultJvmArgs = properties.filterKeys { it in systemProps }.map { "-D${it.key}=${it.value}" }
}

