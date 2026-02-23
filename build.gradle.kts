plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "9.2.2"
}

group = "com.github.lukesky19"
version = "1.5.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        name = "PlaceholderAPI Repo"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.7")
    implementation("org.spongepowered:configurate-yaml:4.2.0")
    implementation("org.spongepowered:configurate-gson:4.2.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
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

    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        archiveClassifier.set("")

        relocate("org.spongepowered.configurate", "com.github.lukesky19.skylib.libs.configurate")
        relocate("com.github.gson", "com.github.lukesky19.libs.gson")
        relocate("org.bstats", "com.github.lukesky19.skylib.libs.bstats")
        relocate("com.zaxxer.hikari", "com.github.lukesky19.skylib.libs.hikaricp")
        relocate("com.jeff_media.morepersistentdatatypes", "com.github.lukesky19.skylib.libs.morepersistentdatatypes")
    }

    // This allows usage of @apiNode in javadocs
    javadoc {
        (options as StandardJavadocDocletOptions).tags("apiNote:a:API Note:")
    }

    build {
        dependsOn(shadowJar)
        dependsOn(publishToMavenLocal)
        dependsOn(javadoc)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}