package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.context.storeContext
import io.github.frankois944.googleAnalyticsKMPTracker.events.GAEvents
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LOG
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentSelection
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentSelection.Builder
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentState
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentType
import io.github.frankois944.googleAnalyticsKMPTracker.storage.PersistingStorage
import io.github.frankois944.googleAnalyticsKMPTracker.storage.Preferences
import kotlin.collections.component1
import kotlin.collections.component2

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
            if (isStarted()) return
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

        /**
         * Update Consent
         */
        public fun updateConsent(block: Builder.() -> Unit) {
            val instance = requireNotNull(instance) { REQUIRE_START_ERROR }
            instance.updateConsent(block)
        }

        public val consents: ConsentSelection
            get() {
                val instance = requireNotNull(instance) { REQUIRE_START_ERROR }
                return instance.getConsent()
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

    // <editor-fold desc="UserId">
    private fun userId(): String? =
        Preferences.userId.also {
            LOG.log(LogLevel.Verbose) { "Get User ID: $it" }
        }

    private fun setUserId(id: String?) {
        LOG.log(LogLevel.Info) { "Set User ID: $id" }
        Preferences.userId = id
    }
    // </editor-fold>

    // <editor-fold desc="OptedOut">
    private fun isOptedOut(): Boolean =
        PersistingStorage
            .get("isOptedOut")
            .toBoolean()
            .also { LOG.log(LogLevel.Verbose) { "Get Opted Out: $it" } }

    private fun setIsOptedOut(enabled: Boolean) {
        LOG.log(LogLevel.Info) { "Set IsOptedOut: $enabled" }
        Preferences.isOptedOut = enabled
    }
    // </editor-fold>

    // <editor-fold desc="Consent">
    private fun updateConsent(block: Builder.() -> Unit) {
        LOG.log(LogLevel.Info) { "Update Consent" }
        val result =
            Builder()
                .apply {
                    Preferences.consent.decisions.forEach { (type, state) -> set(type, state) }
                }.apply(block)
                .build()
        Preferences.consent = result
    }

    public fun getConsent(): ConsentSelection = Preferences.consent
    // </editor-fold>

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
