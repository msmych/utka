plugins {
    `maven-publish`
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("io.ktor.plugin") version "3.0.0"
}

val kitVersion: String by project
val junitVersion: String by project
val jwtVersion: String by project
val assertjVersion: String by project

repositories {
    mavenCentral()
    maven {
        name = "KitPackages"
        url = uri("https://maven.pkg.github.com/msmych/kit")
        credentials {
            username = "utka"
            password = project.findProperty("ghPackagesRoToken") as? String ?: System.getenv("GH_PACKAGES_RO_TOKEN")
        }
    }
}

dependencies {
    api("io.ktor:ktor-network-tls-certificates")
    api("io.ktor:ktor-serialization-kotlinx-json")
    api("io.ktor:ktor-server-auth")
    api("io.ktor:ktor-server-core")
    api("io.ktor:ktor-server-freemarker")
    api("io.ktor:ktor-server-html-builder")
    api("io.ktor:ktor-server-netty")
    api("io.ktor:ktor-server-sse")
    api("io.ktor:ktor-server-status-pages")

    api("io.ktor:ktor-client-cio")
    api("io.ktor:ktor-client-content-negotiation")
    api("io.ktor:ktor-client-core")

    api("com.auth0:java-jwt:$jwtVersion")

    implementation("uk.matvey:kit:$kitVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    enabled = false
}

group = "uk.matvey"
version = project.findProperty("releaseVersion") as? String ?: "0.1.0-SNAPSHOT"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = "utka"
            version = project.version.toString()

            from(components["java"])

            pom {
                name = "Utka"
                description = "Kotlin HTTP utilities"
                url = "https://github.com/msmych/utka"

                licenses {
                    license {
                        name = "Apache-2.0"
                        url = "https://spdx.org/licenses/Apache-2.0.html"
                    }
                }
                developers {
                    developer {
                        id = "msmych"
                        name = "Matvey Smychkov"
                        email = "realsmych@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/msmych/utka.git"
                    developerConnection = "scm:git:ssh://github.com/msmych/utka.git"
                    url = "https://github.com/msmych/utka"
                }
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/msmych/utka")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GH_PACKAGES_RW_TOKEN")
                }
            }
        }
    }
}
