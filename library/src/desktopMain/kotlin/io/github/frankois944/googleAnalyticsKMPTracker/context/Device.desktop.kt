package io.github.frankois944.googleAnalyticsKMPTracker.context

import io.github.frankois944.googleAnalyticsKMPTracker.util.Size
import oshi.SystemInfo
import java.awt.GraphicsDevice
import java.awt.GraphicsEnvironment
import java.util.Locale

internal actual object Device {
    private val si = SystemInfo()
    private val ge: GraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment()
    private val devices: GraphicsDevice? = ge.defaultScreenDevice

    actual val category: String = "desktop"
    actual val language: String
        get() = Locale.getDefault().language + "-" + Locale.getDefault().country
    actual val screenResolution: Size
        get() =
            devices?.displayMode.let {
                Size(width = it?.width?.toLong() ?: 0, height = it?.height?.toLong() ?: 0)
            }
    actual val operatingSystem: String = si.operatingSystem.family
    actual val operatingSystemVersion: String = si.operatingSystem.versionInfo.version
    actual val model: String = si.hardware.computerSystem.model
    actual val brand: String = si.hardware.computerSystem.manufacturer
    actual val browser: String = operatingSystem
    actual val browserVersion: String? = operatingSystemVersion
}
