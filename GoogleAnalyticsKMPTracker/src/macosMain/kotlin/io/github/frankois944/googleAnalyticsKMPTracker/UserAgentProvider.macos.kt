@file:OptIn(ExperimentalForeignApi::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker

import kotlinx.cinterop.ExperimentalForeignApi

internal actual object UserAgentProvider {
    actual fun getUserAgent(): String = "Darwin/${Device.softwareId} (Macintosh; ${Device.model}; Mac OS X ${Device.osVersion})"

    actual fun getClientHint(): String =
        (
            "{" +
                "\"versionNum\": \"42\"" +
                "," +
                "\"versionBuild\": \"1\"" +
                "}"
        )
}
