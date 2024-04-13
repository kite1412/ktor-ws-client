val ktor_version: String by project

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

group = "com.nrr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // added
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}