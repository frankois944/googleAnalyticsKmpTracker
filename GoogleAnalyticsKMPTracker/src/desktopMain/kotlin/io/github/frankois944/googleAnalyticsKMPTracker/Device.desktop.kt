@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker

import oshi.SystemInfo
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.util.Locale

internal actual object Device {

    actual val isBrowser: Boolean = false
    private val si = SystemInfo()
    var ge: GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
    var devices: GraphicsDevice? = ge.defaultScreenDevice

    actual val model: String = si.hardware.computerSystem.model
    actual val operatingSystem: String = si.operatingSystem.family
    actual val osVersion: String = si.operatingSystem.versionInfo.version
    actual val screenSize: Size
        get() =
            devices?.displayMode.let {
                Size(width = it?.width?.toLong() ?: 0, height = it?.height?.toLong() ?: 0)
            }
    actual val nativeScreenSize: Size? = null

    actual val softwareId: String? = si.operatingSystem.versionInfo.codeName

    actual val language: String?
        get() = Locale.getDefault().language + "-" + Locale.getDefault().country

    actual val identifier: String? = null
    actual val category: String = "desktop"
    actual val browser: String? = operatingSystem
    actual val browserVersion: String? = null
    actual val currentUserAgent: String? = null
    actual val brand: String = si.hardware.computerSystem.manufacturer
}
