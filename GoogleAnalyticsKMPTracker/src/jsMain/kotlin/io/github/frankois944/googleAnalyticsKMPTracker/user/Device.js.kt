@file:OptIn(ExperimentalWasmJsInterop::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker.user

import io.github.frankois944.googleAnalyticsKMPTracker.height
import io.github.frankois944.googleAnalyticsKMPTracker.userAgent
import io.github.frankois944.googleAnalyticsKMPTracker.width

internal actual object Device {
    actual val isBrowser: Boolean = true

    actual val model: String
        get() = "jsBrowser"
    actual val operatingSystem: String = userAgent
    actual val osVersion: String = userAgent
    actual val screenSize: Size
        get() = Size(width.toLong(), height.toLong())
    actual val nativeScreenSize: Size? = null
    actual val softwareId: String? = null

    actual val language: String? = null

    actual val identifier: String? = null
    actual val category: String = "browser"
    actual val browser: String? = null
    actual val browserVersion: String? = null
    actual val currentUserAgent: String? = null
    actual val brand: String = "Browser"
}
