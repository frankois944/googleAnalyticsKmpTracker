@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker


internal actual object Device {
    actual val model: String
        get() = "wasmJs"
    actual val operatingSystem: String = userAgent
    actual val osVersion: String = userAgent
    actual val screenSize: Size
        get() = Size(width.toLong(), height.toLong())
    actual val nativeScreenSize: Size? = null
    actual val softwareId: String? = null

    actual val language: String? =
        languages
            .toArray()
            .firstOrNull { it.toString().contains("-") }
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
