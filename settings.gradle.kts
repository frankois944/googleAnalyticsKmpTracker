pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GoogleAnalytics-kmp-tracker"
include(":GoogleAnalyticsKMPTracker")
include(":database")
include(":core")
