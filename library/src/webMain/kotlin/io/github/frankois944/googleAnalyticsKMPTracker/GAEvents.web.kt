@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import co.touchlab.kermit.Logger
import kotlin.js.ExperimentalWasmJsInterop

internal actual class GAEvents actual constructor() {
    actual constructor(
        measurementId: String,
        url: String?,
        apiSecret: String?,
        context: Any?,
    ) : this() {
        loadGtagJS(measurementId)
    }

    actual fun sendEvent(
        eventName: String,
        params: Map<String, Any>,
    ) {
        Logger.d {
            "[EVENT]Sending event: $eventName with params: $params"
        }
        valueToJsType(params)?.let {
            sendEvent(eventName, it)
        } ?: run {
            sendEvent(eventName)
        }
    }

    actual fun config(
        configName: String,
        params: Map<String, Any>,
    ) {
        Logger.d {
            "[CONFIG]Configuring config: $configName with params: $params"
        }
        valueToJsType(params)?.let {
            config(configName, it)
        } ?: run {
            config(configName)
        }
    }

    actual fun set(
        parameterName: String,
        params: Map<String, Any>,
    ) {
        Logger.d {
            "[SET]Set parameter: $parameterName with params: $params"
        }
        valueToJsType(params)?.let {
            set(parameterName, it)
        } ?: run {
            set(parameterName)
        }
    }

    actual fun consent(
        consentArgs: Map<String, Boolean>,
        consentParams: Map<String, Any>,
    ) {
        Logger.d {
            "[CONSENT]Updating consent: $consentArgs with params: $consentParams"
        }
        valueToJsType(consentArgs)?.let { args ->
            valueToJsType(consentParams)?.let { params ->
                consent(args, params)
            } ?: run {
                consent(args)
            }
        }
    }
}
