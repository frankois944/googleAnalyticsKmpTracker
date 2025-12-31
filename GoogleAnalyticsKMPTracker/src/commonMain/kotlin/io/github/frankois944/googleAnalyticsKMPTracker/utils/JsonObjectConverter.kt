package io.github.frankois944.googleAnalyticsKMPTracker.utils

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

internal fun Map<String, Any>.toJsonObject(): JsonObject {
    return buildJsonObject {
        for ((k, v) in this@toJsonObject) {
            val jsonValue = when (v) {
                is Double -> JsonPrimitive(v)
                is String -> JsonPrimitive(v)
                is Int -> JsonPrimitive(v)
                is Boolean -> JsonPrimitive(v)
                is Long -> JsonPrimitive(v)
                is Float -> JsonPrimitive(v)
                else -> JsonPrimitive(v.toString())
            }
            put(k, jsonValue)
        }
    }
}
