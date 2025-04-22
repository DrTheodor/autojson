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

val jmhIncludes: Any? = findProperty("jmhIncludes")

jmh {
    if (jmhIncludes != null) {
        includes.set(listOf(jmhIncludes.toString()))
    }
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
    dependsOn(tasks.dokkaGenerate)
    dependsOn(tasks["publishLibPublicationToGhRepository"])
}

dokka {
    val docsFolder = layout.projectDirectory.dir("docs").dir("public")
    val dokkaAssets = layout.projectDirectory.dir("assets").dir("dokka")

    val name = "DrTheo_"
    val repo = "https://github.com/DrTheodor/autojson"
    val personalLink = "https://theo.is-a.dev/"

    moduleName.set("autojson")

    dokkaPublications.html {
        outputDirectory.set(docsFolder.dir("reference"))
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
    }

    dokkaSourceSets.configureEach {
        includes.from("README.md")
        jdkVersion.set(17)

        externalDocumentationLinks.register("jdk17") {
            val jdkDocs = "https://docs.oracle.com/en/java/javase/17/docs/api"
            url("$jdkDocs/")
            packageListUrl("$jdkDocs/element-list")
        }

        sourceLink {
            val path = "src/main/java"
            localDirectory.set(file(path))
            remoteUrl("$repo/blob/main/$path")
            remoteLineSuffix.set("#L")
        }
    }
    pluginsConfiguration.html {
        customStyleSheets.from(dokkaAssets.file("dokka.css"))
        customAssets.from(dokkaAssets.file("logo-icon-white.svg"), dokkaAssets.file("logo-icon.svg"))

        val footerClass = "footer--link_personal"
        footerMessage.set("(c) <a class=\"$footerClass\" href=\"$personalLink\">$name</a>")
    }
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
}

tasks["publishLibPublicationToGhRepository"].finalizedBy("pushMavenRepo")