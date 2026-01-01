package io.github.frankois944.googleAnalyticsKMPTracker.user

import java.util.Locale

internal actual object UserLocation {
    actual val city: String?
        get() = null
    actual val regionId: String?
        get() = Locale.getDefault().country.takeIf { it.isNotEmpty() }
    actual val countryId: String?
        get() = Locale.getDefault().country.takeIf { it.isNotEmpty() }
    actual val subcontinentId: String?
        get() = null
    actual val continentId: String?
        get() = null
}