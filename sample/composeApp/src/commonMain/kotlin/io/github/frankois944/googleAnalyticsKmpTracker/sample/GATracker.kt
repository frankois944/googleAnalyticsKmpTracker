package io.github.frankois944.googleAnalyticsKmpTracker.sample

import io.github.frankois944.googleAnalyticsKMPTracker.DefaultGATrackerLogger
import io.github.frankois944.googleAnalyticsKMPTracker.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.Tracker

object GATracker {

    fun create(context: Any? = null) : Tracker {
        current = Tracker.create(
            measurementId = "G-H59YDQ0C2M",
            apiSecret = "EnM7zNlXQfyWAzloGqxVMg",
            context = context
        ).also {
            it.logger = DefaultGATrackerLogger(minLevel = LogLevel.Verbose)
        }
        return current
    }
    lateinit var current: Tracker
}