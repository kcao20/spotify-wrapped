pluginManagement {
    resolutionStrategy {//for sarah's build
        eachPlugin {
            if (requested.id.id == "com.android.application") {
                useVersion("8.2.1")
            }
        }
    }
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
plugins {
    id("com.android.application") version "8.2.1" apply false
    // or for library projects: id("com.android.library") version "8.3.1" apply false
}
rootProject.name = "Spotify Wrapped"
include(":app")
