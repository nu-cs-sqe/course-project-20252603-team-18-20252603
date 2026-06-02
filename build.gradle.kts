import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension

plugins {
    id("java")
    id("checkstyle")
    id("com.github.spotbugs") version "6.0.25"
    id("info.solidsoft.pitest") version "1.15.0"
    jacoco
}

group = "nu.csse.sqe"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.easymock:easymock:5.2.0")
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.9.3")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

tasks.compileJava {
    options.release = 11
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.required.set(true)

        html.outputLocation.set(
            layout.buildDirectory.dir("reports/jacoco")
        )
    }
}

tasks.check {
    dependsOn(tasks.pitest)
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required = false
        html.required = true
        html.stylesheet = resources.text.fromFile("config/xsl/checkstyle-noframes-severity-sorted.xsl")
    }
}

configure<CheckstyleExtension> {
    isIgnoreFailures = false
}

pitest {
    junit5PluginVersion.set("1.2.1")

    targetClasses.set(listOf("model.*", "view.*"))
    targetTests.set(listOf("model.*", "view.*"))

    threads.set(4)

    outputFormats.set(setOf("HTML"))

    timestampedReports.set(false)
}

jacoco {
    toolVersion = "0.8.11"
}