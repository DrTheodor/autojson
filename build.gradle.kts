plugins {
    id("java")
    id("me.champeau.jmh") version "0.7.2"
    id("maven-publish")
}

group = "dev.drtheo"
version = "0.0.5"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.google.code.gson:gson:2.8.9")
    jmh("com.google.code.gson:gson:2.8.9")

    compileOnly("org.jetbrains:annotations:26.0.2")
}

tasks.test {
    useJUnitPlatform()
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

task<Exec>("pushRepo") {
    val path = layout.projectDirectory.dir("../maven-repo")
    commandLine("$path/deploy.bat")
}

tasks["publishLibPublicationToGhRepository"].dependsOn("pushRepo")