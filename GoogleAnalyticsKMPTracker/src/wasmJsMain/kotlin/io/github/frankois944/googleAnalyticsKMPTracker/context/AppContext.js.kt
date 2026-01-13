package io.github.frankois944.googleAnalyticsKMPTracker.context

import io.github.frankois944.googleAnalyticsKMPTracker.getClientId
import io.github.frankois944.googleAnalyticsKMPTracker.loadGtagJS

internal actual fun storeContext(context: Any?, measurementId: String) {
    println("START SCRIPT\n")
    loadGtagJS(measurementId)
    getClientId(measurementId)
    println("END SCRIPT\n")
}
