@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker.context

import io.github.frankois944.googleAnalyticsKMPTracker.loadGtagJS
import io.github.frankois944.googleAnalyticsKMPTracker.mapToJsObject
import io.github.frankois944.googleAnalyticsKMPTracker.sendEvent

internal actual fun storeAndLoadContext(context: Any?, measurementId: String) {
    loadGtagJS(measurementId)
    sendEvent("wasm_my_custom_event", mapToJsObject(buildMap {
        put("param1".toJsString(), "data1".toJsString())
        put("param2".toJsString(), "data2".toJsString())
    }))
}
