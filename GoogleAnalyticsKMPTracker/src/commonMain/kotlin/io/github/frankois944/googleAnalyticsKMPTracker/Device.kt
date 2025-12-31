@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class Size(
    val width: Long,
    val height: Long,
)


internal expect object Device {

    val model: String

    val operatingSystem: String

    val osVersion: String

    val screenSize: Size

    val nativeScreenSize: Size?

    val softwareId: String?

    val language: String?

    val identifier: String?

    val category: String

    val browser: String?
    
    val browserVersion: String?

    val currentUserAgent: String?
    val brand: String
}

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
