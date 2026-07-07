import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("idea")
    id("fabric-loom") version "1.9-SNAPSHOT"
}

repositories {
    mavenLocal()
    maven { url = uri("https://maven.fabricmc.net") }
    maven { url = uri("https://maven.terraformersmc.com/") }
    maven { url = uri("https://ueaj.dev/maven") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://maven.isxander.dev/releases") }

    maven { url = uri("https://maven.quiltmc.org/repository/release") }
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.properties["minecraft_version"]}")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${rootProject.properties["minecraft_version"]}:${rootProject.properties["parchment_release"]}@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${rootProject.properties["fabric_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.properties["fabric_api_version"]}")

    // Json entity models
    modImplementation("maven.modrinth:jsonem:${rootProject.properties["jsonem_version"]}")
    include("maven.modrinth:jsonem:${rootProject.properties["jsonem_version"]}")

    implementation("de.javagl:obj:0.4.0")
    include("de.javagl:obj:0.4.0")

    // Controlify
    modCompileOnly("dev.isxander:controlify:${rootProject.properties["controlify_version"]}-fabric")

    implementation(project.project(":common").sourceSets.getByName("main").output)
}

loom {
    runs {
        named("client") {
            client()
            configName = "Client"
            ideConfigGenerated(true)
            runDir("run")
        }
        named("server") {
            server()
            configName = "Server"
            ideConfigGenerated(true)
            runDir("run")
        }
    }

    accessWidenerPath = project(":common").loom.accessWidenerPath
}

tasks {
    withType<JavaCompile> {
        // include common code in compiled jar
        source(project(":common").sourceSets.main.get().allSource)
    }

    // put all artifacts in the right directory
    withType<Jar> {
        destinationDirectory = rootDir.resolve(project.name).resolve("build").resolve("libs")
    }
    withType<RemapJarTask> {
        destinationDirectory = rootDir.resolve(project.name).resolve("build").resolve("libs")
    }

    javadoc { source(project(":common").sourceSets.main.get().allJava) }

    processResources {
        from(project(":common").sourceSets.main.get().resources)

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to rootProject.properties["mod_version"]))
        }
    }

    named("compileTestJava").configure {
        enabled = false
    }

    named("test").configure {
        enabled = false
    }
}