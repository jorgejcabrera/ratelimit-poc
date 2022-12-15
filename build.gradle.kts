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
val caffeineCacheVersion = "3.1.1"
val mockkVersion = "1.13.2"
val junitVersion = "5.9.0"

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.dropwizard:dropwizard-core:$dropwizardCoreVersion")
    implementation("com.github.ben-manes.caffeine:caffeine:${caffeineCacheVersion}")

    // Test
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testImplementation("io.mockk:mockk:${mockkVersion}")

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