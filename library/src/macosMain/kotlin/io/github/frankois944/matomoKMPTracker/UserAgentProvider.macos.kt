@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker

import io.ktor.http.encodeURLParameter
import kotlinx.cinterop.ExperimentalForeignApi

internal actual object UserAgentProvider {
    actual fun getUserAgent(): String {
        val device = Device.create()
        return "Darwin/${device.softwareId} (Macintosh; ${device.model}; Mac OS X ${device.osVersion})"
    }

    actual fun getClientHint(): String =
        (
            "{" +
                "\"versionNum\": \"42\"" +
                "," +
                "\"versionBuild\": \"1\"" +
                "}"
        )
}
