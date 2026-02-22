package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.context.storeContext
import io.github.frankois944.googleAnalyticsKMPTracker.events.GAEvents
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LOG
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.storage.PreferenceStorage

public class GATracker private constructor(
    config: GATrackerConfig,
) {
    internal val eventManager: GAEvents

    public companion object {
        private var instance: GATracker? = null

        /**
         * Starts the Google Analytics tracker with the provided configuration.
         */
        public fun start(config: GATrackerConfig) {
            instance = GATracker(config)
        }

        /**
         * Returns true if the tracker has been started, false otherwise.
         */
        public fun isStarted(): Boolean = instance != null

        /**
         * Defines if the user opted out of tracking. When set to true, every event
         * will be discarded immediately.
         */
        public var isOptedOut: Boolean
            get() {
                val instance = requireNotNull(instance) { REQUIRE_START_ERROR }
                return instance.isOptedOut()
            }
            set(value) {
                val instance = requireNotNull(instance) { REQUIRE_START_ERROR }
                instance.setIsOptedOut(value)
            }

        /**
         * Sets the user ID for tracking purposes.
         * This property is persisted between app launches.
         */
        public var userId: String?
            get() {
                val instance = requireNotNull(instance) { REQUIRE_START_ERROR }
                return instance.userId()
            }
            set(value) {
                val instance = requireNotNull(instance) { REQUIRE_START_ERROR }
                instance.setUserId(value)
            }

        public fun trackView(viewName: String) {
            val instance = requireNotNull(instance) { REQUIRE_START_ERROR }
            instance.trackView(viewName)
        }
    }

    init {
        LOG.minLevel = config.logLevel
        LOG.log(LogLevel.Debug) { "Initializing GA tracker with measurementId: ${config.measurementId}" }
        eventManager =
            GAEvents(
                measurementId = config.measurementId,
                url = config.url,
                apiSecret = config.apiSecret,
            )
        storeContext(config.context)
        setIsOptedOut(config.isOptedOut)
    }

    private fun userId(): String? =
        PreferenceStorage.get("userId").also {
            LOG.log(LogLevel.Verbose) { "Get User ID: $it" }
        }

    private fun setUserId(id: String?) {
        LOG.log(LogLevel.Info) { "Set User ID: $id" }
        PreferenceStorage.set("userId", id)
    }

    private fun isOptedOut(): Boolean =
        PreferenceStorage
            .get("isOptedOut")
            .toBoolean()
            .also { LOG.log(LogLevel.Verbose) { "Get Opted Out: $it" } }

    private fun setIsOptedOut(enabled: Boolean) {
        LOG.log(LogLevel.Info) { "Set IsOptedOut: $enabled" }
        PreferenceStorage.set("isOptedOut", enabled.toString())
    }

    private fun trackView(viewName: String) {
        LOG.log(LogLevel.Debug) { "Track View: $viewName" }
        eventManager.sendEvent(
            "page_view",
            buildMap {
                put("page_title", viewName)
                userId?.let { put("client_id", it) }
            },
        )
    }
}
