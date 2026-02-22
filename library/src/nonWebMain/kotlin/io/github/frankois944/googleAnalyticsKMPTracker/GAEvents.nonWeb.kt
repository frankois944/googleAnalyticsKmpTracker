package io.github.frankois944.googleAnalyticsKMPTracker

internal actual class GAEvents actual constructor() {
    actual fun sendEvent(
        eventName: String,
        params: Map<String, Any>,
    ) {
    }

    actual fun config(
        configName: String,
        params: Map<String, Any>,
    ) {
    }

    actual fun set(
        parameterName: String,
        params: Map<String, Any>,
    ) {
    }

    actual fun consent(
        consentArgs: Map<String, Boolean>,
        consentParams: Map<String, Any>,
    ) {
    }

    actual constructor(
        measurementId: String,
        url: String?,
        apiSecret: String?,
        context: Any?,
    ) : this() {
        TODO("Not yet implemented")
    }
}
