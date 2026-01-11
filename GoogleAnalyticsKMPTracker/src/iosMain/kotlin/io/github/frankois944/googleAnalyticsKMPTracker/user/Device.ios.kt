@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.user

import kotlinx.cinterop.ExperimentalForeignApi
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
import platform.UIKit.UIDevice
import platform.UIKit.UIScreen
import platform.darwin.sysctlbyname
import platform.posix.size_tVar
import platform.posix.uname
import platform.posix.utsname

internal actual object Device {

    actual val isBrowser: Boolean = false

    actual val model: String = getPlatform()
    actual val operatingSystem: String = "iOS"
    actual val osVersion: String = UIDevice.currentDevice.systemVersion
    actual val screenSize: Size
        get() =
            UIScreen
                .mainScreen()
                .bounds
                .useContents {
                    Size(width = size.width.toLong(), height = size.height.toLong())
                }
    actual val nativeScreenSize: Size?
        get() =
            UIScreen
                .mainScreen()
                .nativeBounds
                .useContents {
                    Size(width = size.width.toLong(), height = size.height.toLong())
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

    actual val language: String?
    get() = NSLocale.preferredLanguages.firstOrNull() as? String

    actual val identifier: String? = NSBundle.mainBundle.bundleIdentifier

    private fun getPlatform(): String =
        NSProcessInfo().environment["SIMULATOR_MODEL_IDENTIFIER"]?.toString() ?: memScoped {
            // Get the size needed
            // First call to determine the size
            val name = "hw.machine"
            val sizePtr = alloc<size_tVar>()
            if (sysctlbyname(name, null, sizePtr.ptr, null, 0UL) != 0) {
                return UIDevice.currentDevice.model
            }

            // Allocate memory for the result
            val size = sizePtr.value.toInt()
            val buffer = allocArray<kotlinx.cinterop.ByteVar>(size)

            // Second call to actually get the data
            if (sysctlbyname(name, buffer, sizePtr.ptr, null, 0UL) != 0) {
                return UIDevice.currentDevice.model
            }

            return buffer.toKString()
        }

    actual val category: String = "mobile"
    actual val browser: String? = operatingSystem
    actual val browserVersion: String? = null
    actual val currentUserAgent: String? = null
    actual val brand: String = "Apple Inc."
}
