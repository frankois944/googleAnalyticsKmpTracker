package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.http.builder

import io.github.frankois944.googleAnalyticsKMPTracker.context.Device
import io.github.frankois944.googleAnalyticsKMPTracker.util.Size
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun getGADevice(
    language: String?,
    screenSize: Size,
): JsonObject =
    buildJsonObject {
        put("category", Device.category)
        language?.let { put("language", it) }
        put("screen_resolution", "${screenSize.width}x${screenSize.height}")
        put("operating_system", Device.operatingSystem)
        put("operating_system_version", Device.operatingSystemVersion)
        put("model", Device.model)
        put("brand", Device.brand)
        put("browser", Device.browser)
        Device.browserVersion?.let { put("browser_version", it) }
    }
