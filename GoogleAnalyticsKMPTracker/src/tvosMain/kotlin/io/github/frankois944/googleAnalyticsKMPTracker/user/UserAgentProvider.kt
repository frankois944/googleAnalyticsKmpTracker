package io.github.frankois944.googleAnalyticsKMPTracker.user

internal actual object UserAgentProvider {
    actual fun getUserAgent(): String = "Darwin/${Device.softwareId} (${Device.model}; ${Device.operatingSystem} ${Device.osVersion})"

}