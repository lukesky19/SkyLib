dependencies {
    implementation(project(":SkyLib-Common"))

    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
}

tasks {
    compileJava {
        dependsOn(":SkyLib-Common:jar")
    }

    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        archiveFileName.set("SkyLib-Velocity-${project.version}.jar")
        archiveClassifier.set("")
    }
}