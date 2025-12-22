package io.github.frankois944.googleAnalyticsKMPTracker.sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform