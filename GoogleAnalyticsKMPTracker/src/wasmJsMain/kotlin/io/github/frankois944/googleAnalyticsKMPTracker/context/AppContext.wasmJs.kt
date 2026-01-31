@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker.context

import io.github.frankois944.googleAnalyticsKMPTracker.loadGtagJS

internal actual fun storeAndLoadContext(
    context: Any?,
    measurementId: String,
) {
    loadGtagJS(measurementId)
}
