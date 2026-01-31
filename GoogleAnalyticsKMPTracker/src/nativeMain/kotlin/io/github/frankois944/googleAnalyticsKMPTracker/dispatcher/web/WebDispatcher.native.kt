package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.web

internal actual class WebDispatcher {
    actual fun sendSingleEvent(
        eventName: String,
        params: Map<String, Any>,
    ) {
    }

    actual fun setUserProperty(
        key: String,
        value: Any?,
    ) {
    }

    actual fun setConsent(type: List<Pair<ConsentType, Boolean>>) {
    }
}
