@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker.events

import io.github.frankois944.googleAnalyticsKMPTracker.createEmptyObject
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LOG
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentSelection
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentType
import io.github.frankois944.googleAnalyticsKMPTracker.putObject
import io.github.frankois944.googleAnalyticsKMPTracker.valueToJsType
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.toJsString

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
        setDefaultConsent()
    }

    private fun setDefaultConsent() {
        val args = createEmptyObject()
        ConsentType.entries.forEach { type ->
            putObject(
                args,
                type.key.toJsString(),
                "granted".toJsString(),
            )
        }
        io.github.frankois944.googleAnalyticsKMPTracker.functions
            .consent("default".toJsString(), args)
    }

    actual fun sendEvent(
        eventName: String,
        params: Map<String, Any>,
    ) {
        LOG.log(LogLevel.Debug) {
            "[EVENT]Sending eventName: $eventName with params: $params"
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
            "[CONFIG]Configuring configName: $configName with params: $params"
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
            "[SET]Set parameterName: $parameterName with params: $params"
        }
        valueToJsType(params)?.let {
            io.github.frankois944.googleAnalyticsKMPTracker.functions
                .set(parameterName, it)
        } ?: run {
            io.github.frankois944.googleAnalyticsKMPTracker.functions
                .set(parameterName)
        }
    }

    actual fun consent(selection: ConsentSelection) {
        LOG.log(LogLevel.Debug) {
            "[CONSENT]Updating consentArgs: $selection"
        }
        val args = createEmptyObject()
        selection.decisions.forEach { decision ->
            putObject(
                args,
                decision.key.key.toJsString(),
                decision.value.value.toJsString(),
            )
        }
        io.github.frankois944.googleAnalyticsKMPTracker.functions
            .consent("update".toJsString(), args)
    }
}
