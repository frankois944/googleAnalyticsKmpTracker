@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.useContents
import kotlinx.cinterop.value
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSProcessInfo
import platform.Foundation.preferredLanguages
import platform.WatchKit.WKInterfaceDevice
import platform.darwin.sysctlbyname
import platform.posix.size_tVar
import platform.posix.uname
import platform.posix.utsname

internal actual object Device {

    actual val isBrowser: Boolean = false
    actual val model: String = getPlatform()
    actual val operatingSystem: String = "watchOS"
    actual val osVersion: String = WKInterfaceDevice.currentDevice().systemVersion
    actual val screenSize: Size
        get() =
            WKInterfaceDevice
                .currentDevice()
                .screenBounds
                .useContents {
                    Size(width = size.width.toLong(), height = size.height.toLong())
                }
    actual val nativeScreenSize: Size?
        get() =
            WKInterfaceDevice
                .currentDevice()
                .screenBounds
                .useContents {
                    this.size
                }.let { size ->
                    val scaleFactor = WKInterfaceDevice.currentDevice().screenScale
                    Size(width = (size.width * scaleFactor).toLong(), height = (size.height * scaleFactor).toLong())
                }

    actual val softwareId: String?
        get() {
            memScoped {
                val sysinfo = alloc<utsname>()
                if (uname(sysinfo.ptr) == 0) {
                    return sysinfo.release.toKString().trim()
                }
            }
            return null
        }

    actual val language: String? = NSLocale.preferredLanguages.firstOrNull() as? String

    actual val identifier: String? = NSBundle.mainBundle.bundleIdentifier

    private fun getPlatform(): String {
        return NSProcessInfo().environment["SIMULATOR_MODEL_IDENTIFIER"]?.toString() ?: memScoped {
            // Get the size needed
            // First call to determine the size
            val name = "hw.machine"
            val sizePtr = alloc<size_tVar>()
            if (sysctlbyname(name, null, sizePtr.ptr, null, 0u) != 0) {
                return WKInterfaceDevice.currentDevice().model
            }

            // Allocate memory for the result
            val size = sizePtr.value.toInt()
            val buffer = allocArray<kotlinx.cinterop.ByteVar>(size)

            // Second call to actually get the data
            if (sysctlbyname(name, buffer, sizePtr.ptr, null, 0u) != 0) {
                return WKInterfaceDevice.currentDevice().model
            }

            return buffer.toKString()
        }
    }

    actual val category: String = "smart Watch"
    actual val browser: String? = operatingSystem
    actual val browserVersion: String? = null
    actual val currentUserAgent: String? = null
    actual val brand: String = "Apple Inc."
}
