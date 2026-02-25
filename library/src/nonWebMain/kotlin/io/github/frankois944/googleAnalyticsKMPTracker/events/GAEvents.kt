package io.github.frankois944.googleAnalyticsKMPTracker.events

import io.github.frankois944.googleAnalyticsKMPTracker.logger.LOG
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentSelection

internal actual class GAEvents actual constructor(
    measurementId: String,
    url: String?,
    apiSecret: String?,
) {
    init {
        requireNotNull(apiSecret) { "apiSecret must not be null for non web targets" }
        LOG.log(LogLevel.Debug) {
            "Setup GAEvents with measurementId: $measurementId"
        }
    }

    actual fun sendEvent(
        eventName: String,
        params: Map<String, Any>,
    ) {
    }

    actual fun config(
        configName: String,
        params: Map<String, Any>?,
    ) {
    }

    actual fun set(
        parameterName: String,
        params: Map<String, Any>?,
    ) {
    }

    actual fun consent(selection: ConsentSelection) {
    }
}
