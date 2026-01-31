@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.web

import io.github.frankois944.googleAnalyticsKMPTracker.config
import io.github.frankois944.googleAnalyticsKMPTracker.consent
import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.mapToJsObject
import io.github.frankois944.googleAnalyticsKMPTracker.sendEvent
import io.github.frankois944.googleAnalyticsKMPTracker.set
import io.github.frankois944.googleAnalyticsKMPTracker.valueToJsType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

private val jsonConfig =
    Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

internal actual class WebDispatcher {
    actual fun sendSingleEvent(event: Event) {
        println("sendSingleEvent ${event.eventName} ${event.params}\n")

        event.visitor?.userId?.let { userId ->
            val test = valueToJsType(mapOf("user_id" to userId))
            println("test: $test\n")
            test?.let {
                config("TAG_ID", test)
            }
        }

        /*if (event.properties.isNotEmpty()) {
            val userProperties = jsonConfig.parseToJsonElement(event.properties).jsonArray
            userProperties.forEach { property ->
                val item = property.jsonObject
                val name = item["name"]!!.jsonPrimitive.content
                val value = item["value"]
                println("setting property $name to $value\n")
                setUserProperty(key = name, value = value)
            }
        }
        val eventParams = jsonConfig.parseToJsonElement(event.params).jsonObject
        val params =
            buildMap {
                eventParams.asSequence().take(25).forEach { (key, value) ->
                    put(key.toJsString(), valueToJsType(value))
                }
            }
        val mappedObject = mapToJsObject(params)
        sendEvent(event.eventName, mappedObject)*/
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
            "update".toJsString(),
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
