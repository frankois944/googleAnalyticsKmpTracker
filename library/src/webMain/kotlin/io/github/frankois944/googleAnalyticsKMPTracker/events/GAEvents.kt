@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker.events

import io.github.frankois944.googleAnalyticsKMPTracker.logger.LOG
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.valueToJsType
import kotlin.js.ExperimentalWasmJsInterop

internal actual class GAEvents actual constructor(
    measurementId: String,
    url: String?,
    apiSecret: String?,
) {
    init {
        LOG.log(LogLevel.Debug) { "Starting GoogleAnalytics for javascript" }
        io.github.frankois944.googleAnalyticsKMPTracker.functions.loadGtagJS(measurementId) {
            LOG.log(LogLevel.Debug) { "GoogleAnalytics for javascript loaded" }
        }
    }

    actual fun sendEvent(
        eventName: String,
        params: Map<String, Any>,
    ) {
        LOG.log(LogLevel.Debug) {
            "[EVENT]Sending event: $eventName with params: $params"
        }
        valueToJsType(params)?.let {
            io.github.frankois944.googleAnalyticsKMPTracker.functions
                .sendEvent(eventName, it)
        } ?: run {
            io.github.frankois944.googleAnalyticsKMPTracker.functions
                .sendEvent(eventName)
        }
    }

    actual fun config(
        configName: String,
        params: Map<String, Any>,
    ) {
        LOG.log(LogLevel.Debug) {
            "[CONFIG]Configuring config: $configName with params: $params"
        }
        valueToJsType(params)?.let {
            io.github.frankois944.googleAnalyticsKMPTracker.functions
                .config(configName, it)
        } ?: run {
            io.github.frankois944.googleAnalyticsKMPTracker.functions
                .config(configName)
        }
    }

    actual fun set(
        parameterName: String,
        params: Map<String, Any>,
    ) {
        LOG.log(LogLevel.Debug) {
            "[SET]Set parameter: $parameterName with params: $params"
        }
        valueToJsType(params)?.let {
            io.github.frankois944.googleAnalyticsKMPTracker.functions
                .set(parameterName, it)
        } ?: run {
            io.github.frankois944.googleAnalyticsKMPTracker.functions
                .set(parameterName)
        }
    }

    actual fun consent(
        consentArgs: Map<String, Boolean>,
        consentParams: Map<String, Any>,
    ) {
        LOG.log(LogLevel.Debug) {
            "[CONSENT]Updating consent: $consentArgs with params: $consentParams"
        }
        valueToJsType(consentArgs)?.let { args ->
            valueToJsType(consentParams)?.let { params ->
                io.github.frankois944.googleAnalyticsKMPTracker.functions
                    .consent(args, params)
            } ?: run {
                io.github.frankois944.googleAnalyticsKMPTracker.functions
                    .consent(args)
            }
        }
    }
}
