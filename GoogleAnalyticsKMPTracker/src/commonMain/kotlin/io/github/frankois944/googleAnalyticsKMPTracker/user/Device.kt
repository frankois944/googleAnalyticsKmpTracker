@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker

internal class Size(
    val width: Long,
    val height: Long,
)


internal expect object Device {

    val isBrowser: Boolean

    val model: String

    val operatingSystem: String

    val osVersion: String

    val screenSize: Size

    val nativeScreenSize: Size?

    val softwareId: String?

    val language: String?

    val identifier: String?

    val category: String

    val browser: String?
    
    val browserVersion: String?

    val currentUserAgent: String?
    val brand: String
}


