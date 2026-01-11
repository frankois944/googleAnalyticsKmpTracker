package io.github.frankois944.googleAnalyticsKMPTracker.user

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.currentLocale

internal actual object UserLocation {
    actual val city: String?
        get() = null
    actual val regionId: String?
        get() = NSLocale.currentLocale.objectForKey(NSLocaleCountryCode) as String?
    actual val countryId: String?
        get() = NSLocale.currentLocale.objectForKey(NSLocaleCountryCode) as String?
    actual val subcontinentId: String?
        get() = null
    actual val continentId: String?
        get() = null
}