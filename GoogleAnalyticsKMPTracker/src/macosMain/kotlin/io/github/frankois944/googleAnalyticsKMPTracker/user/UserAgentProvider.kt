package io.github.frankois944.googleAnalyticsKMPTracker.user

internal actual object UserAgentProvider {
    actual fun getUserAgent(): String = "Darwin/${Device.softwareId} (Macintosh; ${Device.model}; Mac OS X ${Device.osVersion})"

}