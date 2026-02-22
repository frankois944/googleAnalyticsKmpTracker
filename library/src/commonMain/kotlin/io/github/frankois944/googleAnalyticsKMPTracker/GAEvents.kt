package io.github.frankois944.googleAnalyticsKMPTracker

internal expect class GAEvents() {
    constructor(
        measurementId: String,
        url: String? = null,
        apiSecret: String? = null,
        context: Any? = null,
    )

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
