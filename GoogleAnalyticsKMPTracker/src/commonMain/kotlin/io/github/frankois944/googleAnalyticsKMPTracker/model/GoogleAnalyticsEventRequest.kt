package io.github.frankois944.googleAnalyticsKMPTracker.model

import kotlinx.serialization.Serializable

@Serializable
internal class GoogleAnalyticsEventRequest(
    val name: String,
    val params: GoogleAnalyticsEventParameterRequest? = null
) {
}
