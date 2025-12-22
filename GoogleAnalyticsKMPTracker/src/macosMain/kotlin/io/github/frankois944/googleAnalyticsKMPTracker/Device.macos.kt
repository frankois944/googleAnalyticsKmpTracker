@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.core.Size
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.useContents
import kotlinx.cinterop.value
import platform.AppKit.NSScreen
import platform.Foundation.NSBundle
import platform.Foundation.NSLocale
import platform.Foundation.NSProcessInfo
import platform.Foundation.preferredLanguages
import platform.darwin.sysctlbyname
import platform.posix.size_tVar
import platform.posix.uname
import platform.posix.utsname

internal actual object Device {
    actual val model: String = getPlatform()
    actual val operatingSystem: String = "Mac OS X"
    actual val osVersion: String =
        NSProcessInfo.processInfo.operatingSystemVersion.useContents {
            "$majorVersion.$minorVersion.$patchVersion"
        }
    actual val screenSize: Size
        get() =
            NSScreen
                .mainScreen()
                ?.frame
                ?.useContents {
                    Size(width = size.width.toLong(), height = size.height.toLong())
                } ?: run { Size(width = 0, height = 0) }

    actual val nativeScreenSize: Size? = null

    actual val language: String? = NSLocale.preferredLanguages.firstOrNull() as? String

    actual val actionUrl: String? = NSBundle.mainBundle.bundleIdentifier

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

    private fun getPlatform(): String {
        memScoped {
            // Try to get hardware model
            val name = "hw.model"
            val sizePtr = alloc<size_tVar>()

            // First call to get required size
            if (sysctlbyname(name, null, sizePtr.ptr, null, 0u) == 0) {
                val size = sizePtr.value.toInt()
                val buffer = allocArray<kotlinx.cinterop.ByteVar>(size)

                // Second call to get actual data
                if (sysctlbyname(name, buffer, sizePtr.ptr, null, 0u) == 0) {
                    return buffer.toKString()
                }
            }

            // Alternative approach: Using uname
            val unameSys = alloc<utsname>()
            if (uname(unameSys.ptr) == 0) {
                return unameSys.machine.toKString()
            }

            // Fallback using NSProcessInfo
            return NSProcessInfo.processInfo.hostName
        }
    }
}
