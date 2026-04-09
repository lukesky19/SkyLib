dependencies {
    compileOnly("org.spongepowered:configurate-yaml:4.2.0")
    compileOnly("org.spongepowered:configurate-gson:4.2.0")
    compileOnly("com.google.code.gson:gson:2.13.2")
    compileOnly("com.google.guava:guava:33.5.0-jre")
    compileOnly("com.zaxxer:HikariCP:7.0.2")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.14.1")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.14.1")
    testImplementation("org.mockito:mockito-junit-jupiter:5.21.0")
}

tasks {
    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        archiveFileName.set("SkyLib-Core-${project.version}.jar")
        archiveClassifier.set("")
    }

    test {
        useJUnitPlatform()
    }
}