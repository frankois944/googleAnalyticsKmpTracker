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

public class GATracker private constructor(
    private val config: GATrackerConfig,
) {
    internal val eventManager: GAEvents

    // <editor-fold desc="Public">
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
            get() = getInstance().getConsent()

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

        /**
         * Track an event with optional parameters.
         *
         * @param eventName the name of the event
         * @param eventParams a map of event parameters
         */
        public fun trackEvent(
            eventName: String,
            eventParams: Map<String, Any> = emptyMap(),
        ) {
            getInstance().trackEvent(eventName, eventParams)
        }

        /**
         * Sets a user property by adding a new property or updating an existing one.
         *
         * @param propertyName The name of the user property. Must be 24 characters or fewer, start with an alphabetic character, and contain only alphanumeric characters or underscores.
         * @param value The value of the user property. Must not exceed 36 characters in length
         */
        public fun setUserProperty(
            propertyName: String,
            value: String? = null,
        ) {
            getInstance().setUserProperty(propertyName, value)
        }
    }
    // </editor-fold>

    // <editor-fold desc="Internal">
    init {
        LOG.minLevel = config.logLevel
        LOG.log(LogLevel.Debug) { "Initializing GA tracker with measurementId: ${config.measurementId}" }
        eventManager =
            GAEvents(
                measurementId = config.measurementId,
                url = config.url,
                apiSecret = config.apiSecret,
                userId = config.userId,
                isOptedOut = config.isOptedOut,
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
        eventManager.config(
            config.measurementId,
            mapOf("user_id" to id),
        )
    }
    // </editor-fold>

    // <editor-fold desc="OptedOut">
    private fun isOptedOut(): Boolean =
        Preferences.isOptedOut
            .also { LOG.log(LogLevel.Verbose) { "Get Opted Out: $it" } }

    private fun setIsOptedOut(enabled: Boolean) {
        LOG.log(LogLevel.Info) { "Set IsOptedOut: $enabled" }
        Preferences.isOptedOut = enabled
        updateConsent {
            set(
                ConsentType.AnalyticsStorage,
                if (enabled) {
                    ConsentState.Denied
                } else {
                    ConsentState.Granted
                },
            )
        }
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

    // <editor-fold desc="Track View">
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
            },
        )
    }
    // </editor-fold>

    // <editor-fold desc="Track Event">
    public fun trackEvent(
        eventName: String,
        eventParams: Map<String, Any> = emptyMap(),
    ) {
        eventManager.sendEvent(
            eventName,
            eventParams,
        )
    }
    // </editor-fold>

    // <editor-fold desc="User Properties">
    private fun setUserProperty(
        propertyName: String,
        value: String? = null,
    ) {
        eventManager.set(
            "user_properties",
            mapOf(propertyName to value),
        )
    }
    // </editor-fold>
    // </editor-fold>
}
