@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker

import io.github.frankois944.matomoKMPTracker.core.Size

internal expect object Device {
    val model: String

    val operatingSystem: String

    val osVersion: String

    val screenSize: Size

    val nativeScreenSize: Size?

    val softwareId: String?

    val language: String?

    val actionUrl: String?
}
