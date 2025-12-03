plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.0.13"
    application
}

group = "sqlgenlib"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.16.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("sqlgenclient.Main")
}

tasks.test {
    useJUnitPlatform()
}

javafx {
    version = "11.0.2"
    modules = listOf("javafx.controls")
}