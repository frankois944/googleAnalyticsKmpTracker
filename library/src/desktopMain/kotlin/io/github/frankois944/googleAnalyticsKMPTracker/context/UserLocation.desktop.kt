package io.github.frankois944.googleAnalyticsKMPTracker.context

import oshi.SystemInfo
import java.util.Locale

internal actual object UserLocation {
    actual val countryId: String?
        get() {
            return SystemInfo().operatingSystem.networkParams.domainName?.let {
                Locale.getDefault().country.takeIf { it.isNotEmpty() }
            } ?: Locale.getDefault().country.takeIf { it.isNotEmpty() }
        }
}
