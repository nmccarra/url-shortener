import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm") version "1.4.31"
    application
}

group = "org.nmccarra1"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "org.nmccarra1.url.shortener.ServerKt"
}

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
}

object V {
    const val kotlinLogging = "1.7.8"
    const val guice = "4.1.0"
    const val jackson = "2.10.4"
    const val logback = "0.1.5"
    const val ktor = "1.3.2"
    const val exposed = "0.25.1"
    const val hikaricp = "3.4.5"
    const val h2 = "1.4.199"
    const val mockk = "1.9.3"
    const val mysqlConnector = "8.0.19"
}

dependencies {
    implementation("mysql:mysql-connector-java:${V.mysqlConnector}")
    implementation("com.h2database:h2:${V.h2}")
    implementation("com.zaxxer:HikariCP:${V.hikaricp}")
    implementation("org.jetbrains.exposed:exposed-core:${V.exposed}")
    implementation("org.jetbrains.exposed:exposed-dao:${V.exposed}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${V.exposed}")
    implementation("org.jetbrains.exposed:exposed-java-time:${V.exposed}")
    implementation("io.ktor:ktor-locations:${V.ktor}")
    implementation("io.ktor:ktor-jackson:${V.ktor}")
    implementation("io.ktor:ktor-client-jackson:${V.ktor}")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:${V.jackson}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:${V.jackson}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:${V.jackson}")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${V.jackson}")
    implementation("ch.qos.logback.contrib:logback-jackson:${V.logback}")
    implementation("ch.qos.logback.contrib:logback-json-classic:${V.logback}")
    implementation("com.google.inject:guice:${V.guice}")
    implementation("io.github.microutils:kotlin-logging:${V.kotlinLogging}")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testImplementation("io.mockk:mockk:${V.mockk}")
    testImplementation("io.ktor:ktor-server-test-host:${V.ktor}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    implementation("io.ktor:ktor-server-netty:1.4.0")
    implementation("io.ktor:ktor-html-builder:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")

        showExceptions = true
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showStackTraces  = true
        showExceptions = true
        showStandardStreams = false
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register<JavaExec>("create-url-shortener-app-database") {
    main = "org.nmccarra1.url.shortener.setup.CreateUrlShortenerAppDatabaseKt"
    classpath = sourceSets["main"].runtimeClasspath
}