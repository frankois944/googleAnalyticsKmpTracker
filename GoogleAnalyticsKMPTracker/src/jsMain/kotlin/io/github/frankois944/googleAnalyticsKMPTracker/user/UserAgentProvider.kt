package io.github.frankois944.googleAnalyticsKMPTracker.user

import io.github.frankois944.googleAnalyticsKMPTracker.userAgent

internal actual object UserAgentProvider {
    actual fun getUserAgent(): String = userAgent

}