plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.6"
}

group = "com.github.lukesky19"
version = "1.3.0.0"

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
    compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    implementation("org.spongepowered:configurate-yaml:4.2.0")
    implementation("org.spongepowered:configurate-gson:4.2.0")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.zaxxer:HikariCP:6.3.2")
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
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        archiveClassifier.set("")

        relocate("org.spongepowered.configurate", "com.github.lukesky19.skylib.libs.configurate")
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