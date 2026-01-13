@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker


internal fun createEmptyObject(): JsAny = js("({})")
internal fun putObject(array: JsAny, key: JsAny, value: JsAny): Unit = js("array[key] = value")

internal fun mapToJsObject(map: Map<JsString, JsString>): JsAny {
    val jsObject = createEmptyObject()
    map.forEach { (key, value) ->
        putObject(jsObject, key, value)
    }
    return jsObject
}
