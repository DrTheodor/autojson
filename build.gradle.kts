import org.gradle.internal.os.OperatingSystem

plugins {
    id("java")
    id("me.champeau.jmh") version "0.7.2"
    id("maven-publish")
}

group = "dev.drtheo"
version = "0.1.0-dev.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    jmh("com.google.code.gson:gson:2.8.9")

    compileOnly("org.jetbrains:annotations:26.0.2")
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "gh"
            url = uri(layout.projectDirectory.dir("../maven-repo"))
        }
    }
}

task<Exec>("pushMavenRepo") {
    val suffix = if (OperatingSystem.current().isWindows) "bat" else "sh"
    val path = layout.projectDirectory.dir("../maven-repo")
    commandLine("$path/deploy.$suffix")
}

task("justPublish") {
    dependsOn(tasks.javadoc)
    dependsOn(tasks["publishLibPublicationToGhRepository"])
}

tasks.javadoc {
    setDestinationDir(layout.projectDirectory.dir("docs").dir("public").asFile)
}

tasks["publishLibPublicationToGhRepository"].finalizedBy("pushMavenRepo")