import org.gradle.internal.os.OperatingSystem
import java.time.Year

plugins {
    id("java")
    id("me.champeau.jmh") version "0.7.2"
    id("org.jetbrains.dokka") version "2.0.0"
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

    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:2.0.0")

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

dokka {
    val docsFolder = layout.projectDirectory.dir("docs").dir("public")
    val assets = layout.projectDirectory.dir("assets")
    val dokkaAssets = assets.dir("dokka")

    moduleName.set("autojson")

    dokkaPublications.html {
        outputDirectory.set(docsFolder.dir("dokka"))
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
    }

    dokkaSourceSets.configureEach {
        includes.from("README.md")
        jdkVersion.set(17)

        externalDocumentationLinks.register("jdk17") {
            url("https://docs.oracle.com/en/java/javase/17/docs/api/")
            packageListUrl("https://docs.oracle.com/en/java/javase/17/docs/api/element-list")
        }

        sourceLink {
            localDirectory.set(file("src/main/java"))
            remoteUrl("https://github.com/DrTheodor/autojson/blob/main/")
            remoteLineSuffix.set("#L")
        }
    }
    pluginsConfiguration.html {
        customStyleSheets.from(dokkaAssets.file("dokka.css"))
        customAssets.from(dokkaAssets.file("logo-icon-white.svg"), dokkaAssets.file("logo-icon.svg"))
        footerMessage.set("(c) DrTheo_")
    }
}

tasks.javadoc {
    val docsFolder = layout.projectDirectory.dir("docs").dir("public")

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

        addStringOption("stylesheetfile", docsFolder.file("javadocs.css").asFile.absolutePath)
    }

    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }

    setDestinationDir(docsFolder.dir("javadocs").asFile)
}

tasks["publishLibPublicationToGhRepository"].finalizedBy("pushMavenRepo")