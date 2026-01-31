@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.web

import io.github.frankois944.googleAnalyticsKMPTracker.consent
import io.github.frankois944.googleAnalyticsKMPTracker.mapToJsObject
import io.github.frankois944.googleAnalyticsKMPTracker.sendEvent
import io.github.frankois944.googleAnalyticsKMPTracker.set
import io.github.frankois944.googleAnalyticsKMPTracker.valueToJsType

internal actual class WebDispatcher {
    actual fun sendSingleEvent(
        eventName: String,
        params: Map<String, Any>,
    ) {
        val params =
            buildMap {
                params.asSequence().take(25).forEach { (key, value) ->
                    put(key.toJsString(), valueToJsType(value))
                }
            }
        val mappedObject = mapToJsObject(params)
        sendEvent(eventName, mappedObject)
    }

    actual fun setUserProperty(
        key: String,
        value: Any?,
    ) {
        if (value != null) {
            set(key, valueToJsType(value))
        } else {
            set(key)
        }
    }

    /**
     * gtag('consent', 'update', {
     *   analytics_storage: 'granted',
     *   ad_storage: 'denied',
     *   ad_user_data: 'denied',
     *   ad_personalization: 'denied'
     * });
     */
    actual fun setConsent(type: List<Pair<ConsentType, Boolean>>) {
        consent(
            "update",
            mapToJsObject(
                buildMap {
                    type.forEach {
                        val value = if (it.second) "granted" else "denied"
                        put(it.first.name.toJsString(), value.toJsString())
                    }
                },
            ),
        )
    }
}
