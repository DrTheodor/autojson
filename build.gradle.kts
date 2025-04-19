import org.gradle.internal.os.OperatingSystem
import java.time.Year

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
    options {
        this as StandardJavadocDocletOptions

        tags = listOf(
            "apiNote:a:API Note:",
            "implSpec:a:Implementation Requirements:",
            "implNote:a:Implementation Note:"
        )

        encoding = "UTF-8"
        docEncoding = "UTF-8"
        charSet = "UTF-8"
        memberLevel = JavadocMemberLevel.PROTECTED

        windowTitle = "AutoJSON API"
        docTitle = "AutoJSON API Documentation"
        bottom = """
            <a href="https://theo.is-a.dev/">DrTheo_</a> © ${Year.now().value}
        """.trimIndent()

        links("https://docs.oracle.com/en/java/javase/17/docs/api/")
    }

    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }

    setDestinationDir(layout.projectDirectory.dir("docs").dir("public").dir("javadocs").asFile)
}

tasks["publishLibPublicationToGhRepository"].finalizedBy("pushMavenRepo")