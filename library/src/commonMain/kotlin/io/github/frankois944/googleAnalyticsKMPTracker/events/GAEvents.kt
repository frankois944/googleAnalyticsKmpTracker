package io.github.frankois944.googleAnalyticsKMPTracker.events

internal expect class GAEvents(
    measurementId: String,
    url: String? = null,
    apiSecret: String? = null,
) {
    fun sendEvent(
        eventName: String,
        params: Map<String, Any> = emptyMap(),
    )

    fun config(
        configName: String,
        params: Map<String, Any> = emptyMap(),
    )

    fun set(
        parameterName: String,
        params: Map<String, Any> = emptyMap(),
    )

    fun consent(
        consentArgs: Map<String, Boolean>,
        consentParams: Map<String, Any> = emptyMap(),
    )
}
