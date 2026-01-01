package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher

import io.github.frankois944.googleAnalyticsKMPTracker.Device
import io.github.frankois944.googleAnalyticsKMPTracker.Size
import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.getGADeviceJsonObject
import io.github.frankois944.googleAnalyticsKMPTracker.user.getGAUserConsent
import io.github.frankois944.googleAnalyticsKMPTracker.user.getGAUserLabelJsonObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put


private val jsonConfig = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
}

internal fun Event.getGaBody(): JsonObject {
    return buildJsonObject {
        setBaseInfo(this@getGaBody)
        put("events", setEvent(this@getGaBody))
        // TODO: remove when debug is over
        put("validation_behavior", "ENFORCE_RECOMMENDATIONS")
    }
}

internal fun List<Event>.getGaBody(): JsonObject {
    if (isEmpty()) {
        return JsonObject(emptyMap())
    }
    return buildJsonObject {
        setBaseInfo(first())
        put("events", buildJsonArray {
            this@getGaBody.forEach { event ->
                add(setEvent(event))
            }
        })
        // TODO: remove when debug is over
        put("validation_behavior", "ENFORCE_RECOMMENDATIONS")
    }
}

private fun JsonObjectBuilder.setBaseInfo(event: Event) {
    put("client_id", event.visitor.clientId)
    if (!event.visitor.userId.isNullOrEmpty()) {
        put("user_id", event.visitor.userId)
    }
    getGAUserConsent(event.adUserData, event.adPersonalization)?.let {
        put("consent", it)
    }
    if (!Device.isBrowser) {
        put("user_location", getGAUserLabelJsonObject())
        put(
            "device", getGADeviceJsonObject(
                event.language,
                Size(event.screenResolutionWidth, event.screenResolutionHeight)
            )
        )
    } else {
        put("user_agent", Device.currentUserAgent)
    }
}

private fun setEvent(event: Event): JsonObject {
    return buildJsonObject {
        put("name", event.eventName)
        put("timestamp_micros", event.dateCreatedInMs * 1000)
        val eventParams = jsonConfig.parseToJsonElement(event.params).jsonObject
        put("params", buildJsonObject {
            put("session_id", event.sessionId)
            // You must include the engagement_time_msec and session_id parameters in order for user activity
            // to display in reports like Realtime.
            put("engagement_time_msec", event.dateCreatedInMs - event.lastEventTimeStampInMs)
            eventParams.asSequence().take(25).forEach { (key, value) ->
                put(key, value)
            }
        })
    }
}