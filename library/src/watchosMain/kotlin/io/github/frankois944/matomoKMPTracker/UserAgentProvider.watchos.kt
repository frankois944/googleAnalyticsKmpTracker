@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker

internal actual object UserAgentProvider {
    actual fun getUserAgent(): String {
        val device = Device.create()
        return "Darwin/${device.softwareId} (${device.model}; ${device.operatingSystem} ${device.osVersion})"
    }
}
