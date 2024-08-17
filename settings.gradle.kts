rootProject.name = "Glabay-Studios-Server"

plugins {
    id("de.fayard.refreshVersions") version("0.51.0")
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            files("../gradle/libs.versions.toml")
        }
    }
}