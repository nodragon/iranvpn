pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Psiphon (optional): add dependency implementation("ca.psiphon:psiphontunnel:2.0.35")
        maven { url = uri("https://raw.githubusercontent.com/Psiphon-Labs/psiphon-tunnel-core-Android-library/master") }
    }
}
rootProject.name = "IranVPN"
include(":app")
