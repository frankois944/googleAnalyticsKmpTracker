package io.github.frankois944.googleAnalyticsKmpTracker.sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform