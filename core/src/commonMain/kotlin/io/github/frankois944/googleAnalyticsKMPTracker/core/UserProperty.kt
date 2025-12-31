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
        name.also {
            require(it.length <= 24) {
                "Property name must be 24 characters or fewer, got ${name.length}"
            }
            require(it.isNotEmpty() && name.first().isLetter()) {
                "Property name must start with an alphabetic character, got $name"
            }
            require(it.all { it.isLetterOrDigit() || it == '_' }) {
                "Property name can only contain alphanumeric characters and underscores, got $name"
            }
        },
        when (value) {
            is Double -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is Int -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Long -> JsonPrimitive(value)
            else -> JsonPrimitive(value.toString())
        }.also {
            require(it.content.length <= 36) {
                "Event value must be 36 characters or fewer, got ${value.toString().length}"
            }
        }
    )
}
