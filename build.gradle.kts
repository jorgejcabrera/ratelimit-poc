import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "com.poc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val dropwizardCoreVersion = "2.1.2"

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.dropwizard:dropwizard-core:$dropwizardCoreVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    //mainClass.set("MainKt")
    mainClassName = "delivery.MainKt"

}

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:5.2.0")
    }
}

apply(plugin = "kotlin")
apply(plugin = "com.github.johnrengelman.shadow")

val run by tasks.getting(JavaExec::class) {
    args("server", "config/servercfg.yaml")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        //mergeServiceFiles()
        archiveBaseName.set("ratelimit-poc")
        exclude("META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.SF")
        isZip64 = true
        manifest {
            attributes(mapOf("Main-Class" to "delivery.MainKt"))
        }
    }
    named<JavaExec>("run") {
        args = listOf("server", "config/servercfg.yaml")
    }

    register("e2e-test", Test::class) {
        useJUnitPlatform()
    }

    register("integration-test", Test::class) {
        useJUnitPlatform()
    }
}