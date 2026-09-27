plugins {
    java
    id("net.fabricmc.fabric-loom-remap") version("1.17.+")
    id("ploceus") version("1.17.+")
}

group = "dev.rdh"
version = providers.environmentVariable("GITHUB_SHA")
    .flatMap { sha -> providers.gradleProperty("mod_version").map { "$it-g${sha.take(7)}" } }
    .orElse(providers.gradleProperty("mod_version"))
    .get()

java.toolchain {
    languageVersion = JavaLanguageVersion.of(25)
}

@Suppress("MayBeConstant")
object Versions {
    val minecraft = "1.8.9"
    val feather = "2"
    val osl = "0.20.3"
    val fabric = "0.19.3"
    val celeritas = "2.4.0-dev.5"
}

loom {
    accessWidenerPath = file("src/main/resources/sarcio.classtweaker")

    runs.named("client") {
        jvmArguments.add("-Dmixin.debug.export=true")
    }
}

ploceus {
    setIntermediaryGeneration(2)
}

val celery = sourceSets.create("celery") {
    compileClasspath += sourceSets.main.map { it.output + it.compileClasspath }.get()
}

repositories {
    maven("https://maven.taumc.org/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:${Versions.minecraft}")
    mappings(ploceus.featherMappings(Versions.feather))

    modImplementation("net.fabricmc:fabric-loader:${Versions.fabric}")
    ploceus.dependOsl(Versions.osl)
    add(celery.compileOnlyConfigurationName, "org.embeddedt.celeritas:celeritas-common:${Versions.celeritas}")
}

tasks.assemble {
    dependsOn("remapJar")
}

tasks.jar {
    from(celery.output)
}

tasks.processResources {
    val v = project.version
    inputs.property("version", v)

    filesMatching("fabric.mod.json") {
        expand("version" to v)
    }
}
