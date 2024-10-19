plugins {
    id("java")
    `java-library`
}

group = "com.das747"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    modularity.inferModulePath.set(true)
}

dependencies {
    testImplementation("org.testng:testng:7.9.0")
    testImplementation("org.mockito:mockito-core:3.+")
    // https://github.com/square/retrofit/issues/4210
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("org.slf4j:slf4j-api:2.0.16")
    compileOnly("org.jetbrains:annotations:25.0.0")
    testCompileOnly("org.jetbrains:annotations:25.0.0")
}

tasks.test {
    useTestNG()
}

