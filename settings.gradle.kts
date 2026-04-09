rootProject.name = "SkyLib"

include(":SkyLib-Common")
project(":SkyLib-Common").projectDir = file("common")
include(":SkyLib-Paper")
project(":SkyLib-Paper").projectDir = file("paper")
include(":SkyLib-Velocity")
project(":SkyLib-Velocity").projectDir = file("velocity")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        // Common
        mavenCentral()
        google()

        // Paper
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        // Velocity
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}