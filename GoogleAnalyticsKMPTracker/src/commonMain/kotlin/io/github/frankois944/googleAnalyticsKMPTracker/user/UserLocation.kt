package io.github.frankois944.googleAnalyticsKMPTracker.user

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