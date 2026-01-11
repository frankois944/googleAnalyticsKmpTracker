package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.http.builder

import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.user.Device
import io.github.frankois944.googleAnalyticsKMPTracker.user.Size
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put


private val jsonConfig = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
}
internal fun Event.getGaBody(isValidationMode: Boolean): JsonObject {
    return buildJsonObject {
        setBaseInfo(this@getGaBody)
        put("events", setEvent(this@getGaBody))
        if (isValidationMode) {
            put("validation_behavior", "ENFORCE_RECOMMENDATIONS")
        }
    }
}

internal fun List<Event>.getGaBody(isValidationMode: Boolean): JsonObject {
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
        if (isValidationMode) {
            put("validation_behavior", "ENFORCE_RECOMMENDATIONS")
        }
    }
}

private fun JsonObjectBuilder.setBaseInfo(event: Event) {
    val visitor = event.visitor ?: return
    put("client_id", visitor.clientId)
    if (!visitor.userId.isNullOrEmpty()) {
        put("user_id", visitor.userId)
    }
    setUserProperties(event)
    getGAUserConsent(event.adUserData, event.adPersonalization)?.let {
        put("consent", it)
    }
    if (!Device.isBrowser) {
        put("user_location", getGAUserLocation())
        put(
            "device", getGADevice(
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
            put("debug_mode", true)
            eventParams.asSequence().take(25).forEach { (key, value) ->
                put(key, value)
            }
        })
    }
}

private fun JsonObjectBuilder.setUserProperties(event: Event) {
    if (event.properties.isNotEmpty()) {
        val userProperties = jsonConfig.parseToJsonElement(event.properties).jsonArray
        if (userProperties.isNotEmpty()) {
            val propertyValues = buildJsonObject {
                userProperties.forEach { property ->
                    val item = property.jsonObject
                    val name = item["name"]!!.jsonPrimitive.content
                    val value = item["value"]
                    put(
                        name,
                        buildJsonObject {
                            if (value != null) {
                                put("value", value)
                            }
                        }
                    )
                }
            }
            put("user_properties", propertyValues)
        }
    }
}