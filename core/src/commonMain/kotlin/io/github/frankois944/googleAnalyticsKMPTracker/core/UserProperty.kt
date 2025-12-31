package io.github.frankois944.googleAnalyticsKMPTracker.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive

@Serializable
/**
 * For more information on custom dimensions visit https://piwik.org/docs/custom-dimensions/
 */
public class UserProperty(
    /**
     *  The index of the dimension. A dimension with this index must be setup in the Matomo backend.
     */
    public val name: String,
    /**
     *  The value you want to set for this dimension.
     */
    public val value: JsonPrimitive,
) {
    public constructor(name: String, value: Any) : this(
        name,
        when (value) {
            is Double -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is Int -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            else -> JsonPrimitive(value.toString())
        }
    )
}
