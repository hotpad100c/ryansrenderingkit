
val unobfuscated:Boolean = stonecutter.current.version.toString().contains("-snapshot-")



plugins {
    id("fabric-loom")
    //id("fabric-loom-remapped")
    // `maven-publish`
    //id("me.modmuss50.mod-publish-plugin").version("0.3.5")
    id("signing")
    id ("com.vanniktech.maven.publish").version("0.35.0")
}

version = "${property("mod.version")}+${stonecutter.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava = when {
    stonecutter.eval(stonecutter.current.version, ">=26.1") -> JavaVersion.VERSION_25
    stonecutter.eval(stonecutter.current.version, ">=1.20.6") -> JavaVersion.VERSION_21
    stonecutter.eval(stonecutter.current.version, ">=1.18") -> JavaVersion.VERSION_17
    stonecutter.eval(stonecutter.current.version, ">=1.17") -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    /**
     * Fetches only the required Fabric API modules to not waste time downloading all of them for each version.
     * @see <a href="https://github.com/FabricMC/fabric">List of Fabric API modules</a>
     */
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, property("deps.fabric_api") as String))
    }

    minecraft("com.mojang:minecraft:${stonecutter.current.version}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    fapi("fabric-lifecycle-events-v1","fabric-rendering-v1","fabric-command-api-v2", "fabric-resource-loader-v0", "fabric-content-registries-v0")
}
val minecraft = stonecutter.current.version
val accesswidener = when {

    stonecutter.eval(minecraft, "<=1.20.6") -> "1.20.1.accesswidener"
    stonecutter.eval(minecraft, "<=1.21.4") -> "1.21.4.accesswidener"
    else -> "1.21.10.accesswidener"
}
loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection
    accessWidenerPath = rootProject.file("src/main/resources/accesswideners/$accesswidener")
    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true") // Exports transformed classes for debugging
        runDir = "../../run" // Shares the run directory between versions
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

tasks {
    processResources {
        inputs.property("id", project.property("mod.id"))
        inputs.property("name", project.property("mod.name"))
        inputs.property("version", project.property("mod.version"))
        inputs.property("minecraft", project.property("mod.mc_dep"))

        val props = mapOf(
            "id" to project.property("mod.id"),
            "name" to project.property("mod.name"),
            "version" to project.property("mod.version"),
            "minecraft" to project.property("mod.mc_dep"),
            "aw_file" to accesswidener
        )

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

/*
// Publishes builds to Modrinth and Curseforge with changelog from the CHANGELOG.md file
publishMods {
    file = tasks.remapJar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile.get() })
    displayName = "${property("mod.name")} ${property("mod.version")} for ${property("mod.mc_title")}"
    version = property("mod.version") as String
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add("fabric")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null
        || providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(property("mod.mc_targets").toString().split(' '))
        requires {
            slug = "fabric-api"
        }
    }

    curseforge {
        projectId = property("publish.curseforge") as String
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.addAll(property("mod.mc_targets").toString().split(' '))
        requires {
            slug = "fabric-api"
        }
    }
}
 */

// Publishes builds to a maven repository under `com.example:template:0.1.0+mc`
/*
publishing {
    repositories {
        maven("https://mvnrepository.com/artifact/io.github.hotpad100c/ryansrenderingkit/releases") {
            name = "RyansRenderingKit"
            // To authenticate, create `myMavenUsername` and `myMavenPassword` properties in your Gradle home properties.
            // See https://stonecutter.kikugie.dev/wiki/tips/properties#defining-properties
            credentials(PasswordCredentials::class.java)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${property("mod.group")}.${property("mod.id")}"
            artifactId = property("mod.id") as String
            version = project.version.toString()

            from(components["java"])

            pom {
                name = "Ryan's Rendering Kit"
                description = "A powerful rendering utility library for Fabric"
                url = "https://github.com/hotpad100c/ryansrenderingkit"
                licenses {
                    license {
                        name = "MIT"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        id = "hotpad100c"
                        name = "Ryan100C"
                    }
                }
                scm {
                    connection = "scm:git:github.com:hotpad100c/ryansrenderingkit.git"
                    developerConnection = "scm:git:github.com:hotpad100c/ryansrenderingkit.git"
                    url = "https://github.com/hotpad100c/ryansrenderingkit"
                }
            }
        }
    }
}*/
mavenPublishing {
    publishToMavenCentral()

    signAllPublications()
    coordinates(
        "io.github.hotpad100c",
        "ryansrenderingkit",
        project.version.toString()
    )
    pom {
        name.set("Ryans Rendering Kit")
        description.set("A Fabric rendering utility library for Minecraft mods.")
        url.set("https://github.com/hotpad100c/ryansrenderingkit")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        scm {
            url.set("https://github.com/hotpad100c/ryansrenderingkit")
            connection.set("scm:git:https://github.com/hotpad100c/ryansrenderingkit.git")
            developerConnection.set("scm:git:ssh://git@github.com:hotpad100c/ryansrenderingkit.git")
        }

        developers {
            developer {
                id.set("hotpad100c")
                name.set("Ryan100C")
                email.set("hotpad100c@gmail.com")
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        findProperty("signing.keyId") as String,
        file("C:\\Users\\Ryan\\.gnupg\\private.key").readText(),
        findProperty("signing.password") as String
    )
    sign(publishing.publications)
}