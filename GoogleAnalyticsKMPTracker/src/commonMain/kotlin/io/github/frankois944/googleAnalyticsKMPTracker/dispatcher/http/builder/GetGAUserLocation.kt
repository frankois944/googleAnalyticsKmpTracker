package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.http.builder

import io.github.frankois944.googleAnalyticsKMPTracker.user.UserLocation
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun getGAUserLocation(
    city: String? = null,
    regionId: String? = null,
    countryId: String? = null,
    subcontinentId: String? = null,
    continentId: String? = null,
): JsonObject {
    return buildJsonObject {
        (city ?: UserLocation.city)?.let { put("city", it) }
        (regionId ?: UserLocation.regionId)?.let { put("region_id", it) }
        (countryId ?: UserLocation.countryId)?.let { put("country_id", it) }
        (subcontinentId ?: UserLocation.subcontinentId)?.let { put("subcontinent_id", it) }
        (continentId ?: UserLocation.continentId)?.let { put("continent_id", it) }
    }
}