package io.github.frankois944.googleAnalyticsKMPTracker.user

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal expect object UserLocation {
    /**
     * The city's name. If the city is in the US, also set country_id and region_id
     * so Google Analytics can properly map the city name to a city ID.
     */
    val city: String?

    /**
     * The ISO 3166 country and subdivision. For example, US-CA, US-AR, CA-BC, GB-LND, CN-HK.
     */
    val regionId: String?

    /**
     * The country in ISO 3166-1 alpha-2 format. For example, US, AU, ES, FR.
     */
    val countryId: String?

    /**
     * The subcontinent in UN M49 format. For example, 011, 021, 030, 039.
     */
    val subcontinentId: String?

    /**
     * The continent in UN M49 format. For example, 002, 019, 142, 150.
     */
    val continentId: String?
}

internal fun getGAUserLabelJsonObject(
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