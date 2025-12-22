@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker

import oshi.SystemInfo
import oshi.software.os.OperatingSystem
import java.util.Locale

internal actual object UserAgentProvider {
    actual fun getUserAgent(): String {
        val systemInfo = SystemInfo()
        val os: OperatingSystem = systemInfo.operatingSystem
        val versionInfo = os.versionInfo
        val family = os.family.lowercase(Locale.ROOT)
        val arch = System.getProperty("os.arch")

        return when {
            family.contains("windows") -> {
                val majorVersion = versionInfo.version?.split(".")?.firstOrNull() ?: "10"
                "Mozilla/5.0 (Windows NT $majorVersion.0; Win64; $arch)"
            }

            family.contains("mac") -> {
                val version = versionInfo.version?.replace(".", "_") ?: "10_15"
                "Mozilla/5.0 (Macintosh; Intel Mac OS X $version)"
            }

            family.contains("linux") -> {
                val distro = versionInfo.codeName ?: "Linux"
                val version = versionInfo.version ?: "unknown"
                "Mozilla/5.0 (X11; Linux $arch; $distro $version)"
            }

            else -> {
                // Fallback
                "Mozilla/5.0 (X11; $family $arch)"
            }
        }
    }

    actual fun getClientHint(): String =
        (
            "{" +
                "\"versionNum\": \"42\"" +
                "," +
                "\"versionBuild\": \"1\"" +
                "}"
        )
}
