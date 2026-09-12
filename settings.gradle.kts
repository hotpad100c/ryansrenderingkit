pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.8.3"
}

stonecutter {
    create(rootProject) {
        // See https://stonecutter.kikugie.dev/wiki/start/#choosing-minecraft-versions
        versions(
            "1.19.4",
            "1.20.1",
            "1.20.6",
            "1.21.1",
            "1.21.3",
            "1.21.4",
            "1.21.5",
            "1.21.6",
            "1.21.9",
            "1.21.10",
            "1.21.11",
        ).buildscript("build.gradle.kts")
        versions(
            "26.1",
            "26.2"
        ).buildscript("unobfuscated.gradle.kts")
        vcsVersion = "1.21.4"
    }
}

rootProject.name = "Ryan'sServerRenderingKit"