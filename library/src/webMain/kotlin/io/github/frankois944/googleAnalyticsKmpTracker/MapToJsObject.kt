@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKmpTracker

/*import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull*/
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.JsAny
import kotlin.js.JsString
import kotlin.js.js
import kotlin.js.toJsBigInt
import kotlin.js.toJsBoolean
import kotlin.js.toJsNumber
import kotlin.js.toJsString

internal fun createEmptyObject(): JsAny = js("({})")

internal fun putObject(
    array: JsAny,
    key: JsAny,
    value: JsAny?,
): Unit = js("array[key] = value")

internal fun mapToJsObject(map: Map<JsString, JsAny?>): JsAny {
    val jsObject = createEmptyObject()
    map.forEach { (key, value) ->
        putObject(jsObject, key, value)
    }
    return jsObject
}

/*internal fun jsonValueToJsType(value: JsonElement): JsAny? {
    if (value is JsonPrimitive) {
        return when {
            value.isString -> value.toString().toJsString()
            value.booleanOrNull != null -> value.booleanOrNull?.toJsBoolean()
            value.intOrNull != null -> value.intOrNull?.toJsNumber()
            value.longOrNull != null -> value.longOrNull?.toJsBigInt()
            value.doubleOrNull != null -> value.doubleOrNull?.toJsNumber()
            value.floatOrNull != null -> value.floatOrNull?.toDouble()?.toJsNumber()
            else -> null
        }
    }
    return null
}*/

internal fun valueToJsType(value: Any): JsAny? =
    when (value) {
        is Int -> {
            value.toJsNumber()
        }

        is Long -> {
            value.toJsBigInt()
        }

        is Double -> {
            value.toJsNumber()
        }

        is Float -> {
            value.toDouble().toJsNumber()
        }

        is Boolean -> {
            value.toJsBoolean()
        }

        is String -> {
            value.toJsString()
        }

        is Map<*, *> -> {
            val array = createEmptyObject()
            value.map { item ->
                putObject(
                    array,
                    item.toString().toJsString(),
                    item.value?.let { valueToJsType(it) },
                )
            }
            array
        }

        else -> {
            null
        }
    }
