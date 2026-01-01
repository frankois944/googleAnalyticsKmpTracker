package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.http.builder

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun getGAUserConsent(
    adUserData: Boolean?,
    adPersonalization: Boolean?,
): JsonObject? {
    if (adUserData == null && adPersonalization == null) {
        return null
    }
    return buildJsonObject {
        adUserData?.let { put("ad_user_data", if (it) "GRANTED" else "DENIED") }
        adPersonalization?.let { put("ad_personalization", if (it) "GRANTED" else "DENIED") }
    }
}