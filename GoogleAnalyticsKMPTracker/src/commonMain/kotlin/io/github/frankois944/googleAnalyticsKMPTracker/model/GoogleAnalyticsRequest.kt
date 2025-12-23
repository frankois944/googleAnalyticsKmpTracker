@file:OptIn(ExperimentalSerializationApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class GoogleAnalyticsRequest(
    @SerialName("app_instance_id")
    val appInstanceId: String,
    val events: List<GoogleAnalyticsEventRequest>,
)