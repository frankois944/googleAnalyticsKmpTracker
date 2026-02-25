package io.github.frankois944.googleAnalyticsKMPTracker.events

import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentSelection

internal expect class GAEvents(
    measurementId: String,
    url: String? = null,
    apiSecret: String? = null,
    userId: String?,
    isOptedOut: Boolean,
) {
    fun sendEvent(
        eventName: String,
        params: Map<String, Any> = emptyMap(),
    )

    fun config(
        configName: String,
        params: Map<String, Any?> = emptyMap(),
    )

    fun config(
        configName: String,
        value: String?,
    )

    fun set(
        parameterName: String,
        params: Map<String, Any?>? = null,
    )

    fun set(
        parameterName: String,
        value: String?,
    )

    fun consent(selection: ConsentSelection)
}
