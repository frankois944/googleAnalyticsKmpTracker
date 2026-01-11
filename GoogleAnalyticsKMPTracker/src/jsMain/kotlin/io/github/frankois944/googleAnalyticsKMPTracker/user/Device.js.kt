@file:OptIn(ExperimentalWasmJsInterop::class)
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker.user

import io.github.frankois944.googleAnalyticsKMPTracker.height
import io.github.frankois944.googleAnalyticsKMPTracker.hostname
import io.github.frankois944.googleAnalyticsKMPTracker.languages
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

    actual val language: String? =
        languages
            .toArray()
            .firstOrNull { it.contains("-") }
            .toString()

    actual val identifier: String? = hostname
    actual val category: String = "browser"
    actual val browser: String? = userAgent.let { ua ->
        when {
            ua.contains("Firefox") && !ua.contains("Seamonkey") -> "Firefox"
            ua.contains("Seamonkey") -> "Seamonkey"
            ua.contains("Chrome") && !ua.contains("Chromium") && !ua.contains("Edg") -> "Chrome"
            ua.contains("Chromium") -> "Chromium"
            ua.contains("Safari") && !ua.contains("Chrome") && !ua.contains("Chromium") -> "Safari"
            ua.contains("OPR") || ua.contains("Opera") -> "Opera"
            ua.contains("Edg") -> "Edge"
            ua.contains("MSIE") || ua.contains("Trident") -> "Internet Explorer"
            else -> "Unknown"
        }
    }
    actual val browserVersion: String? = null
    actual val currentUserAgent: String? = userAgent
    actual val brand: String = "Browser"
}
