@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker

internal expect object UserAgentProvider {
    fun getUserAgent(): String

    fun getClientHint(): String
}
