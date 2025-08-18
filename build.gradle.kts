plugins {
    id("java")
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application {
    mainClass.set("sqlgen.examples.GISMUBI_28341")
}

group = "org.example"
version = "0.0.2-pre-alpha"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation ("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.0")
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}

tasks.test {
    useJUnitPlatform()
}