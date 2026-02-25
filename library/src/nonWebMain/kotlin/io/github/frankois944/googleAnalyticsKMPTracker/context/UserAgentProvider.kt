package io.github.frankois944.googleAnalyticsKMPTracker.context

internal expect object UserAgentProvider {
    val userAgent: String
}
