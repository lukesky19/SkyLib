dependencies {
    implementation(project(":SkyLib-Common"))

    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly("me.clip:placeholderapi:2.11.7")

    compileOnly("org.bstats:bstats-bukkit:3.2.1")
    compileOnly("com.jeff-media:MorePersistentDataTypes:2.4.0")
}

tasks {
    compileJava {
        dependsOn(":SkyLib-Common:jar")
    }

    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        archiveFileName.set("SkyLib-Paper-${project.version}.jar")
        archiveClassifier.set("")
    }
}