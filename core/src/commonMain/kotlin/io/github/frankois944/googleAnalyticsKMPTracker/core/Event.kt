@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class, ExperimentalSerializationApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.core

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@Serializable
public class Event(
    public val uuid: String,
    public val dateCreatedInMs: Long,
    public var visitor: Visitor?,
    /**
     * JSON encoded string
     */
    public val properties: String,
    public val screenResolutionWidth: Long,
    public val screenResolutionHeight: Long,
    public var lastEventTimeStampInMs: Long = 0,
    public val sessionId: Long,
    public val measurementId: String,
    public val language: String?,
    public val eventName: String,
    public val adUserData: Boolean? = null,
    public val adPersonalization: Boolean? = null,
    /**
     * JSON encoded string
     */
    public val params: String,
) {
    override fun toString(): String =
        """Event(
            |  uuid='$uuid',
            |  params=$params,
            |  visitor=$visitor,
            |  language=$language,
            |  screenResolutionWidth=$screenResolutionWidth,
            |  screenResolutionHeight=$screenResolutionHeight
            |  eventName=$eventName,
            |  firebaseAppId=$measurementId,
            |  sessionId=$sessionId
            |  lastEventTimeStampInMs=$lastEventTimeStampInMs
            |)
        """.trimMargin()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event) return false
        return uuid == other.uuid &&
                visitor == other.visitor &&
                params == other.params &&
                language == other.language &&
                screenResolutionWidth == other.screenResolutionWidth &&
                screenResolutionHeight == other.screenResolutionHeight &&
                eventName == other.eventName &&
                measurementId == other.measurementId &&
                sessionId == other.sessionId &&
                properties == other.properties &&
                lastEventTimeStampInMs == other.lastEventTimeStampInMs &&
                dateCreatedInMs == other.dateCreatedInMs
    }

    override fun hashCode(): Int {
        var result = 31 * uuid.hashCode()
        result = 31 * result + visitor.hashCode()
        result = 31 * result + params.hashCode()
        result = 31 * result + (language?.hashCode() ?: 0)
        result = 31 * result + screenResolutionWidth.hashCode()
        result = 31 * result + screenResolutionHeight.hashCode()
        result = 31 * result + eventName.hashCode()
        result = 31 * result + properties.hashCode()
        result = 31 * result + measurementId.hashCode()
        result = 31 * result + sessionId.hashCode()
        result = 31 * result + lastEventTimeStampInMs.hashCode()
        result = 31 * result + dateCreatedInMs.hashCode()
        return result
    }

    public companion object
}
