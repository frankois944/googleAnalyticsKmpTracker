package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.context.storeContext
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LOG
import io.github.frankois944.googleAnalyticsKMPTracker.logger.loggerConfig
import io.github.frankois944.googleAnalyticsKMPTracker.storage.PreferenceStorage

public class GATracker private constructor(
    config: GATrackerConfig,
) {
    init {
        loggerConfig.minSeverity = config.logLevel.toKermitSeverity()
        storeContext(config.context)
        setIsOptedOut(config.isOptedOut)
    }

    private fun userId(): String? =
        PreferenceStorage.get("userId").also {
            LOG.v { "Get User ID: $it" }
        }

    private fun setUserId(id: String?) {
        LOG.i { "Set User ID: $id" }
        PreferenceStorage.set("userId", id)
    }

    private fun isOptedOut(): Boolean =
        PreferenceStorage
            .get("isOptedOut")
            .toBoolean()
            .also { LOG.v { "Get Opted Out: $it" } }

    private fun setIsOptedOut(enabled: Boolean) {
        LOG.i { "Set IsOptedOut: $enabled" }
        PreferenceStorage.set("isOptedOut", enabled.toString())
    }

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

        public fun sendEvent(userId: String?) {
            val instance = requireNotNull(instance) { REQUIRE_START_ERROR }
        }
    }
}
