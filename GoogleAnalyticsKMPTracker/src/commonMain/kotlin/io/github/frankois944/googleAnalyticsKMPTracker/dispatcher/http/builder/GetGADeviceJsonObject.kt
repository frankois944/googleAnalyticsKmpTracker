package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.http.builder

import io.github.frankois944.googleAnalyticsKMPTracker.Device
import io.github.frankois944.googleAnalyticsKMPTracker.Size
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun getGADeviceJsonObject(language: String?, screenSize: Size) : JsonObject {
    return buildJsonObject {
        put("category", Device.category)
        language?.let {  put("language", it) }
        put("screenResolution", "${screenSize.width}x${screenSize.height}")
        put("operatingSystem", Device.operatingSystem)
        put("operatingSystemVersion", Device.osVersion)
        put("model", Device.model)
        put("brand", Device.brand)
        Device.browser?.let { put("browser", it) }
        Device.browserVersion?.let { put("browserVersion", it) }
    }
}