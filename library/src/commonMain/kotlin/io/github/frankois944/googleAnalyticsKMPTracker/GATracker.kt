package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.context.storeContext
import io.github.frankois944.googleAnalyticsKMPTracker.events.GAEvents
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LOG
import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentSelection
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentSelection.Builder
import io.github.frankois944.googleAnalyticsKMPTracker.storage.PersistingStorage
import io.github.frankois944.googleAnalyticsKMPTracker.storage.Preferences

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
            if (isStarted()) error("Tracker already started")
            instance = GATracker(config)
        }

        /**
         * Returns true if the tracker has been started, false otherwise.
         */
        public fun isStarted(): Boolean = instance != null

        /**
         * Get the tracker instance, ensuring it has been started.
         * Throws an exception if the tracker has not been started.
         */
        @Throws(IllegalStateException::class)
        private fun getInstance() = requireNotNull(instance) { REQUIRE_START_ERROR }

        /**
         * Defines if the user opted out of tracking. When set to true, every event
         * will be discarded immediately.
         */
        public var isOptedOut: Boolean
            get() {
                return getInstance().isOptedOut()
            }
            set(value) {
                getInstance().setIsOptedOut(value)
            }

        /**
         * Sets the user ID for tracking purposes.
         * This property is persisted between app launches.
         */
        public var userId: String?
            get() {
                return getInstance().userId()
            }
            set(value) {
                getInstance().setUserId(value)
            }

        /**
         * Update Consent
         */
        public fun updateConsent(block: Builder.() -> Unit) {
            getInstance().updateConsent(block)
        }

        public val consents: ConsentSelection
            get() {
                return getInstance().getConsent()
            }

        /**
         * Track a view with optional location hierarchy.
         * @param viewName the name of the screen
         * @param viewLocation the location hierarchy of the screen
         *
         * If viewLocation is provided, it will be joined with '/' to form the page_location.
         * If viewLocation is null, the viewName will be used as page_location.
         */
        public fun trackView(
            viewName: String,
            viewLocation: List<String>? = null,
        ) {
            getInstance().trackView(viewName, viewLocation?.joinToString("/"))
        }
    }

    init {
        LOG.minLevel = config.logLevel
        LOG.log(LogLevel.Debug) { "Initializing GA tracker with measurementId: ${config.measurementId}" }
        setUserId(config.userId)
        storeContext(config.context)
        setIsOptedOut(config.isOptedOut)
        eventManager =
            GAEvents(
                measurementId = config.measurementId,
                url = config.url,
                apiSecret = config.apiSecret,
            )
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
        eventManager.consent(result)
    }

    public fun getConsent(): ConsentSelection = Preferences.consent
    // </editor-fold>

    private fun trackView(
        viewName: String,
        viewLocation: String? = null,
    ) {
        LOG.log(LogLevel.Debug) { "Track viewName: $viewName viewLocation: $viewLocation" }
        eventManager.sendEvent(
            "page_view",
            buildMap {
                put("page_title", viewName)
                viewLocation?.let { put("page_location", it) } ?: run {
                    put("page_location", viewName)
                }
                userId?.let { put("client_id", it) }
            },
        )
    }
}
