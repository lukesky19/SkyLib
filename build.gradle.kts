plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "9.4.1"
    id("xyz.jpenilla.gremlin-gradle") version "0.0.9"
}

subprojects {
    apply(plugin = "java-library")

    dependencies {
        // Annotations
        compileOnly("org.jspecify:jspecify:1.0.0")

        // Adventure
        compileOnly("net.kyori:adventure-api:5.2.0")
        compileOnly("net.kyori:adventure-text-logger-slf4j:5.2.0")
        compileOnly("net.kyori:adventure-text-minimessage:5.2.0")

        // Configurate (Configuration)
        compileOnly("org.spongepowered:configurate-yaml:4.2.0")
        compileOnly("org.spongepowered:configurate-gson:4.2.0")

        // HikariCP
        compileOnly("com.zaxxer:HikariCP:7.1.0")

        // Database Drivers
        compileOnly("org.xerial:sqlite-jdbc:3.53.2.0")

        // Message Brokers
        compileOnly("com.rabbitmq:amqp-client:5.33.0")

        // Gremlin (Runtime dependency resolution)
        compileOnly("xyz.jpenilla:gremlin-runtime:0.0.9")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
        withSourcesJar()
        withJavadocJar()
    }

    tasks {
        processResources {
            val props = mapOf("version" to version)
            inputs.properties(props)
            filteringCharset = "UTF-8"
            val filePatterns = listOf("plugin.yml", "paper-plugin.yml")

            filePatterns.forEach { filePattern ->
                filesMatching(filePattern) {
                    expand(props)
                }
            }
        }

        // This allows usage of @apiNode in javadocs
        javadoc {
            (options as StandardJavadocDocletOptions).tags("apiNote:a:API Note:")
        }

        build {
            dependsOn(javadoc)
        }
    }
}

dependencies {
    // Project Modules
    implementation(project(":SkyLib-Common"))
    implementation(project(":SkyLib-Paper"))
    implementation(project(":SkyLib-Velocity"))

    // Shadowed dependencies
    implementation("org.spongepowered:configurate-yaml:4.2.0")
    implementation("org.spongepowered:configurate-gson:4.2.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")
    implementation("com.rabbitmq:amqp-client:5.30.0")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("xyz.jpenilla:gremlin-runtime:0.0.9")

    // Runtime Dependencies
    runtimeDownload("org.xerial:sqlite-jdbc:3.51.3.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
    withJavadocJar()
}

tasks {
    jar {
        archiveClassifier.set("ignored")
    }

    shadowJar {
        dependsOn(":SkyLib-Common:jar")
        dependsOn(":SkyLib-Paper:jar")
        dependsOn(":SkyLib-Velocity:jar")

        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        archiveClassifier.set("")

        relocate("org.bstats", "com.github.lukesky19.skylib.libs.bstats")
        relocate("org.spongepowered.configurate", "com.github.lukesky19.skylib.libs.configurate")
        relocate("com.google.gson", "com.github.lukesky19.skylib.libs.gson")
        relocate("org.bstats", "com.github.lukesky19.skylib.libs.bstats")
        relocate("com.zaxxer.hikari", "com.github.lukesky19.skylib.libs.hikaricp")
        relocate("com.jeff_media.morepersistentdatatypes", "com.github.lukesky19.skylib.libs.morepersistentdatatypes")
        relocate("com.rabbitmq", "com.github.lukesky19.skylib.libs.rabbitmq")
        relocate("xyz.jpenilla", "com.github.lukesky19.skylib.libs.gremlin")
    }

    publishToMavenLocal {
        dependsOn(shadowJar)
        dependsOn(javadoc)
    }

    build {
        dependsOn(shadowJar)
        dependsOn(publishToMavenLocal)
        dependsOn(javadoc)
    }
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            from(components["shadow"])
        }
    }
}