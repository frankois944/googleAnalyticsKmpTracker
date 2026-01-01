package io.github.frankois944.googleAnalyticsKMPTracker.user

internal expect object UserAgentProvider {
    fun getUserAgent(): String
}