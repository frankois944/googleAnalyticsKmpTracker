package io.github.frankois944.googleAnalyticsKMPTracker.events

import io.github.frankois944.googleAnalyticsKMPTracker.EventsHandler
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LOG
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentSelection

internal actual class GAEvents actual constructor(
    measurementId: String,
    url: String?,
    apiSecret: String?,
    userId: String?,
    isOptedOut: Boolean,
) {
    val eventHandler =
        EventsHandler(
            measurementId,
            url,
            apiSecret,
            userId,
            isOptedOut,
        )

    actual fun sendEvent(
        eventName: String,
        params: Map<String, Any>,
    ) {
        LOG.log(LogLevel.Debug) { "Sending event: $eventName with params: $params" }
        eventHandler.sendEvent(eventName, params)
    }

    actual fun config(
        configName: String,
        params: Map<String, Any?>,
    ) {
        LOG.log(LogLevel.Debug) { "Config: $configName with params: $params" }
        eventHandler.config(configName, params)
    }

    actual fun config(
        configName: String,
        value: String?,
    ) {
        LOG.log(LogLevel.Debug) { "Config: $configName with value: $value" }
        eventHandler.config(configName, value)
    }

    actual fun set(
        parameterName: String,
        params: Map<String, Any?>?,
    ) {
        LOG.log(LogLevel.Debug) { "Setting parameter: $parameterName with params: $params" }
        eventHandler.set(parameterName, params)
    }

    actual fun set(
        parameterName: String,
        value: String?,
    ) {
        LOG.log(LogLevel.Debug) { "Setting parameter: $parameterName with value: $value" }
        eventHandler.set(parameterName, value)
    }

    actual fun consent(selection: ConsentSelection) {
        LOG.log(LogLevel.Debug) { "Consent selection: $selection" }
        eventHandler.consent(selection)
    }
}
