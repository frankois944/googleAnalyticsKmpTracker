package io.github.frankois944.googleAnalyticsKMPTracker.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class GoogleAnalyticsEventParameterRequest(
    val actionName: String? = null,
    @SerialName("session_id")
    val sessionId: Long? = null,
    @SerialName("debug_mode")
    val debugMode: Boolean = false,
    @SerialName("timestamp_micros")
    val timestampMicros: Long? = null,
    @SerialName("engagement_time_msec")
    val engagementTimeMsec: Long? = null,
    @SerialName("search_term")
    val searchTerm: String? = null,
    @SerialName("screen_class")
    val screenClass: String? = null,
    @SerialName("screen_name")
    val screenName: String? = null,
)