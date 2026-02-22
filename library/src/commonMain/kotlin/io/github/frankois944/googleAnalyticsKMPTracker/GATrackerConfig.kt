package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel

/**
 * Configuration for the Google Analytics KMP Tracker
 * @param measurementId GA Property ID.
 * @param apiSecret The API Secret from the Google Analytics UI (optional for Web target)
 * @param context valid Android Context (MANDATORY for Android target)
 */
public class GATrackerConfig(
    public val measurementId: String,
    public val apiSecret: String?,
    public var context: Any? = null,
) {
    /**
     * The url of the Google Analytics API endpoint, use `https://region1.google-analytics.com/mp/collect` for europe
     */
    public var url: String? = null

    /**
     * Disable tracking on start, require explicit activation.
     */
    public var isOptedOut: Boolean = false

    /**
     * Log level for the tracker
     */
    public var logLevel: LogLevel = LogLevel.Error
}
