@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.core.UserProperty
import io.github.frankois944.googleAnalyticsKMPTracker.user.Device
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val jsonConfig = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
}

/**
 * Creates a new instance of the [Event] class with the specified parameters.
 *
 * @param tracker The [Tracker] instance containing visitor information, session details, and user properties.
 * @param eventName Name of the event being created.
 * @param params A map of additional parameters associated with the event (max: 25 keys)
 * @param properties A map of custom properties to include in the event (max: 25 keys)
 * @return A new [Event] instance populated with the provided data and system-derived properties.
 */
internal fun Event.Companion.create(
    tracker: Tracker,
    eventName: String,
    params: Map<String, JsonPrimitive> = emptyMap(),
    properties: List<UserProperty> = emptyList(),
): Event {
    for ((name, value) in params) {
        require(name.length <= 40) {
            "Parameter name \"$name\" must be 40 characters or fewer, got ${name.length}"
        }
        require(name.isNotEmpty() && name.first().isLetter()) {
            "Parameter name \"$name\" must start with an alphabetic character"
        }
        require(name.all { it.isLetterOrDigit() || it == '_' }) {
            "Parameter name \"$name\" can only contain alphanumeric characters and underscores"
        }
        require(value.content.length <= 100) {
            "Event value \"$value\" must be 100 characters or fewer, got ${value.content.length}"
        }
    }
    require(eventName.length <= 40) { "Event name \"$eventName\" must be 40 characters or fewer, got ${eventName.length}" }
    require(eventName.isNotEmpty() && eventName.first().isLetter()) {
        "Event name \"$eventName\" must start with an alphabetic character"
    }
    require(eventName.all { it.isLetterOrDigit() || it == '_' }) {
        "Event name \"$eventName\" can only contain alphanumeric characters and underscores"
    }
    return Event(
        dateCreatedInMs = Clock.System.now().toEpochMilliseconds(),
        uuid = Uuid.random().toHexString(),
        visitor = tracker.visitor,
        properties = jsonConfig.encodeToString((tracker.userProperties.getAll() + properties).take(25)),
        screenResolutionWidth = Device.screenSize.width,
        screenResolutionHeight = Device.screenSize.height,
        sessionId = tracker.sessionId,
        measurementId = tracker.measurementId,
        language = Device.language,
        eventName = eventName,
        adUserData = tracker.adUserDataEnabled,
        adPersonalization = tracker.adPersonalizationEnabled,
        params = jsonConfig.encodeToString(params)
    )
}