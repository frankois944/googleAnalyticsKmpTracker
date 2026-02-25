@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker.context

import io.github.frankois944.googleAnalyticsKMPTracker.util.Size

internal expect object Device {
    val category: String

    val language: String

    val screenResolution: Size

    val operatingSystem: String

    val operatingSystemVersion: String

    val model: String

    val brand: String

    val browser: String

    val browserVersion: String?
}
