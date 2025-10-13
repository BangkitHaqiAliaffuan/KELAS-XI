pluginManagement {
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
        // TomTom repository dihilangkan karena memerlukan autentikasi khusus
        // Menggunakan Google Maps SDK sebagai gantinya
    }
}

rootProject.name = "TrashBinFe"
include(":app")
 