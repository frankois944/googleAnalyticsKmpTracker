@file:OptIn(ExperimentalWasmJsInterop::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.core.Size

internal actual object Device {
    actual val model: String
        get() = "jsBrowser"
    actual val operatingSystem: String = userAgent
    actual val osVersion: String = userAgent
    actual val screenSize: Size
        get() = Size(width.toLong(), height.toLong())
    actual val nativeScreenSize: Size? = null
    actual val softwareId: String? = null

    actual val language: String? =
        languages
            .toArray()
            .firstOrNull { it.contains("-") }
            .toString()

    actual val actionUrl: String? = hostname
}
