package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.http.builder

import io.github.frankois944.googleAnalyticsKMPTracker.context.UserLocation
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun getGAUserLocation(
    city: String? = null,
    regionId: String? = null,
    countryId: String? = null,
    subcontinentId: String? = null,
    continentId: String? = null,
): JsonObject =
    buildJsonObject {
        put("country_id", (countryId ?: UserLocation.countryId))
        city?.let { put("city", it) }
        regionId?.let { put("region_id", it) }
        subcontinentId?.let { put("subcontinent_id", it) }
        continentId?.let { put("continent_id", it) }
    }
