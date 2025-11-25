@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.matomoKMPTracker

import io.github.frankois944.matomoKMPTracker.core.Size

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

    actual val actionUrl: String? = hostname
}
