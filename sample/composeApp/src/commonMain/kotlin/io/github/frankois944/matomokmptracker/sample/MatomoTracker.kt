package io.github.frankois944.googleAnalyticsKMPTracker.sample

import io.github.frankois944.googleAnalyticsKMPTracker.DefaultMatomoTrackerLogger
import io.github.frankois944.googleAnalyticsKMPTracker.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.Tracker

object MatomoTracker {

    suspend fun create(context: Any? = null) : Tracker {
        current = Tracker.create(
            url = "https://matomo.spmforkmp.eu/matomo.php",
            siteId = 6,
            context = context
        ).also {
            it.logger = DefaultMatomoTrackerLogger(minLevel = LogLevel.Verbose)
        }
        return current
    }
    lateinit var current: Tracker
}