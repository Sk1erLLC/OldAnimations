import gg.essential.gradle.util.noServerRunConfigs

plugins {
    kotlin("jvm")
    id("gg.essential.multi-version")
    id("gg.essential.defaults")
}

val modGroup: String by project
val modBaseName: String by project
group = modGroup
base.archivesName.set("$modBaseName-${platform.mcVersionStr}")

loom {
    noServerRunConfigs()
    mixin {
        defaultRefmapName.set("mixins.oldanimations.refmap.json")
    }
    runConfigs {
        getByName("client") {
            property("patcher.debugBytecode", "true")
            property("mixin.debug.verbose", "true")
            property("mixin.debug.export", "true")
            property("mixin.dumpTargetOnFailure", "true")
            programArgs("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
            programArgs("--mixin", "mixins.oldanimations.json")
        }
    }
}

repositories {
    mavenLocal()
    maven("https://repo.essential.gg/repository/maven-public/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

val embed by configurations.creating
configurations.implementation.get().extendsFrom(embed)

val jij by configurations.creating
configurations.implementation.get().extendsFrom(jij)

dependencies {
    embed("gg.essential:loader-launchwrapper:1.3.0")

    jij("gg.essential:mixin:0.1.0+mixin.0.8.4")
    jij(annotationProcessor("gg.essential.lib:mixinextras:0.4.0")!!)

    jij("gg.essential:vigilance:306")
    jij(modImplementation("gg.essential:universalcraft-${platform.mcVersionStr}-${platform.loaderStr}:419")!!)
}

tasks.compileKotlin {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn", "-Xno-param-assertions", "-Xjvm-default=all-compatibility")
    }
}

tasks.processResources {
    val expansions = mapOf(
        "version" to version,
        "jars" to provider {
            jij.resolvedConfiguration.resolvedArtifacts.joinToString(",\n") { artifact ->
                val id = artifact.moduleVersion.id
                """
                    {
                        "id": "${id.group}:${id.name}",
                        "version": "${id.version}",
                        "file": "META-INF/jars/${artifact.file.name}"
                    }
                """.trimIndent()
            }
        },
    )
    inputs.property("expansions", expansions)
    filesMatching("essential.mod.json") {
        expand(expansions)
    }
}

tasks.jar {
    dependsOn(jij)
    from(jij.files) {
        into("META-INF/jars")
    }
    from(embed.files.map { zipTree(it) })

    manifest.attributes(mapOf(
        "ModSide" to "CLIENT",
        "FMLCorePluginContainsFMLMod" to "Yes, yes it does",
        "TweakClass" to "gg.essential.loader.stage0.EssentialSetupTweaker",
        "TweakOrder" to "0",
        "MixinConfigs" to "mixins.oldanimations.json"
    ))
}