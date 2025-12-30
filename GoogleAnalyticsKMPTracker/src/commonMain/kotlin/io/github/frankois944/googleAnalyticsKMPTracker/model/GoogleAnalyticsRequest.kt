@file:OptIn(ExperimentalSerializationApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class GoogleAnalyticsRequest(
    @SerialName("client_id")
    val clientId: String,
    val events: List<GoogleAnalyticsEventRequest>,
)